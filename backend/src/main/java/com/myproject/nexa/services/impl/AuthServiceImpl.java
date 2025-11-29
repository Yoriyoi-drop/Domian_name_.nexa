package com.myproject.nexa.services.impl;

import com.myproject.nexa.config.properties.AppProperties;
import com.myproject.nexa.dto.request.LoginRequest;
import com.myproject.nexa.dto.request.RegisterRequest;
import com.myproject.nexa.dto.request.RefreshTokenRequest;
import com.myproject.nexa.dto.response.AuthResponse;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.entities.RefreshToken;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.exceptions.AppException;
import com.myproject.nexa.exceptions.ErrorCode;
import com.myproject.nexa.repositories.UserRepository;
import com.myproject.nexa.security.JwtTokenProvider;
import com.myproject.nexa.services.AuthService;
import com.myproject.nexa.services.TokenService;
import com.myproject.nexa.services.UserService;
import com.myproject.nexa.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final TokenService tokenService;
    private final UserService userService;
    private final AppProperties appProperties;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            User user = (User) authentication.getPrincipal();
            UserResponse userResponse = userService.mapToUserResponse(user);

            // Generate JWT token
            String accessToken = tokenProvider.generateToken(authentication);
            RefreshToken refreshToken = tokenService.createRefreshToken(user);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .expiresIn(appProperties.getJwt().getExpiration())
                    .user(userResponse)
                    .build();
        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            throw new AppException(ErrorCode.AUTH_001, "Invalid credentials");
        }
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new AppException(ErrorCode.USER_002, "Username is already taken");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new AppException(ErrorCode.USER_003, "Email is already in use");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setPhone(registerRequest.getPhone());
        user.setAddress(registerRequest.getAddress());

        // Save user
        User savedUser = userRepository.save(user);
        UserResponse userResponse = userService.mapToUserResponse(savedUser);

        // Generate JWT token
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        savedUser.getUsername(),
                        registerRequest.getPassword()
                );
        String accessToken = tokenProvider.generateToken(authenticationToken);
        RefreshToken refreshToken = tokenService.createRefreshToken(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(appProperties.getJwt().getExpiration())
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        RefreshToken refreshToken = tokenService.findByToken(refreshTokenRequest.getRefreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.VALIDATION_001, "Refresh token not found"));

        if (DateUtil.isExpired(refreshToken.getExpiryDate())) {
            tokenService.deleteByToken(refreshToken.getToken());
            throw new AppException(ErrorCode.AUTH_002, "Refresh token has expired");
        }

        // Get user and generate new tokens
        User user = refreshToken.getUser();
        var authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateToken(authentication);
        RefreshToken newRefreshToken = tokenService.createRefreshToken(user);

        // Mark old refresh token as used
        refreshToken.setUsed(true);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .expiresIn(appProperties.getJwt().getExpiration())
                .user(userService.mapToUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        tokenService.deleteByToken(refreshToken);
    }
}
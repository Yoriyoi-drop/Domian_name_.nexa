package com.myproject.nexa.controllers;

import com.myproject.nexa.annotation.RateLimited;
import com.myproject.nexa.config.properties.AppProperties;
import com.myproject.nexa.dto.request.LoginRequest;
import com.myproject.nexa.dto.request.RegisterRequest;
import com.myproject.nexa.dto.request.RefreshTokenRequest;
import com.myproject.nexa.dto.response.ApiResponse;
import com.myproject.nexa.dto.response.AuthResponse;
import com.myproject.nexa.services.AuthService;
import com.myproject.nexa.utils.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final AppProperties appProperties;

    @PostMapping("/login")
    @RateLimited(limit = 5, window = 300, by = RateLimited.RateLimitType.IP) // 5 attempts per 5 minutes by IP
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());

        AuthResponse authResponse = authService.login(loginRequest);

        // Set HttpOnly cookies for tokens
        cookieUtil.setAccessTokenCookie(response, authResponse.getAccessToken(),
                (int) (appProperties.getJwt().getExpiration() / 1000)); // Convert to seconds
        cookieUtil.setRefreshTokenCookie(response, authResponse.getRefreshToken(),
                (int) (appProperties.getJwt().getRefreshTokenExpiration() / 1000)); // Convert to seconds

        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    @PostMapping("/register")
    @RateLimited(limit = 2, window = 3600, by = RateLimited.RateLimitType.IP) // 2 registrations per hour by IP
    @Operation(summary = "User registration", description = "Register a new user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        log.info("Registration attempt for user: {}", registerRequest.getUsername());

        AuthResponse authResponse = authService.register(registerRequest);

        // Set HttpOnly cookies for tokens
        cookieUtil.setAccessTokenCookie(response, authResponse.getAccessToken(),
                (int) (appProperties.getJwt().getExpiration() / 1000)); // Convert to seconds
        cookieUtil.setRefreshTokenCookie(response, authResponse.getRefreshToken(),
                (int) (appProperties.getJwt().getRefreshTokenExpiration() / 1000)); // Convert to seconds

        return ResponseEntity.ok(ApiResponse.success("Registration successful", authResponse));
    }

    @PostMapping("/refresh")
    @RateLimited(limit = 10, window = 3600, by = RateLimited.RateLimitType.IP) // 10 refresh attempts per hour by IP
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest, HttpServletResponse response) {
        log.info("Token refresh attempt");

        AuthResponse authResponse = authService.refreshToken(refreshTokenRequest);

        // Set HttpOnly cookies for new tokens
        cookieUtil.setAccessTokenCookie(response, authResponse.getAccessToken(),
                (int) (appProperties.getJwt().getExpiration() / 1000)); // Convert to seconds
        cookieUtil.setRefreshTokenCookie(response, authResponse.getRefreshToken(),
                (int) (appProperties.getJwt().getRefreshTokenExpiration() / 1000)); // Convert to seconds

        return ResponseEntity.ok(ApiResponse.success("Token refresh successful", authResponse));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidate refresh token")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestBody RefreshTokenRequest refreshTokenRequest, HttpServletResponse response) {
        log.info("Logout request");

        authService.logout(refreshTokenRequest.getRefreshToken());

        // Clear the token cookies
        cookieUtil.removeTokenCookies(response);

        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
}
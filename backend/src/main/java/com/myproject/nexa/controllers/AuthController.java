package com.myproject.nexa.controllers;

import com.myproject.nexa.annotation.RateLimited;
import com.myproject.nexa.dto.request.LoginRequest;
import com.myproject.nexa.dto.request.RegisterRequest;
import com.myproject.nexa.dto.request.RefreshTokenRequest;
import com.myproject.nexa.dto.response.ApiResponse;
import com.myproject.nexa.dto.response.AuthResponse;
import com.myproject.nexa.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @PostMapping("/login")
    @RateLimited(limit = 5, window = 300, by = RateLimited.RateLimitType.IP) // 5 attempts per 5 minutes by IP
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());

        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/register")
    @RateLimited(limit = 2, window = 3600, by = RateLimited.RateLimitType.IP) // 2 registrations per hour by IP
    @Operation(summary = "User registration", description = "Register a new user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration attempt for user: {}", registerRequest.getUsername());

        AuthResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/refresh")
    @RateLimited(limit = 10, window = 3600, by = RateLimited.RateLimitType.IP) // 10 refresh attempts per hour by IP
    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Token refresh attempt");

        AuthResponse response = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(ApiResponse.success("Token refresh successful", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidate refresh token")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Logout request");

        authService.logout(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
}
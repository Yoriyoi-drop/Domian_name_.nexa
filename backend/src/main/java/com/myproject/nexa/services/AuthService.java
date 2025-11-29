package com.myproject.nexa.services;

import com.myproject.nexa.dto.request.LoginRequest;
import com.myproject.nexa.dto.request.RegisterRequest;
import com.myproject.nexa.dto.request.RefreshTokenRequest;
import com.myproject.nexa.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    void logout(String refreshToken);
}
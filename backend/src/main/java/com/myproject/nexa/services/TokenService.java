package com.myproject.nexa.services;

import com.myproject.nexa.entities.RefreshToken;
import com.myproject.nexa.entities.User;

import java.util.Optional;

public interface TokenService {
    RefreshToken createRefreshToken(User user);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByUser(User user);
    void deleteByToken(String token);
}
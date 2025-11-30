package com.myproject.nexa.services;

import com.myproject.nexa.dto.request.UserUpdateRequest;
import com.myproject.nexa.dto.response.UserResponse;
import com.myproject.nexa.entities.User;

import java.util.Optional;

/**
 * Service interface for user profile operations including viewing and updating
 * user profile information for the current user.
 */
public interface UserProfileService {
    UserResponse getCurrentUser(String username);
    UserResponse updateCurrentUser(String username, UserUpdateRequest request);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
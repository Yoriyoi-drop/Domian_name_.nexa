package com.myproject.nexa.services;

import com.myproject.nexa.dto.request.UserCreateRequest;
import com.myproject.nexa.dto.request.UserUpdateRequest;
import com.myproject.nexa.entities.User;

/**
 * Service interface for user validation operations including validation of
 * user creation and update requests and uniqueness checks.
 */
public interface UserValidationService {
    void validateUserCreation(UserCreateRequest request);
    void validateUserUpdate(Long id, UserUpdateRequest request);
    void validateUsernameUniqueness(String username, Long excludeId);
    void validateEmailUniqueness(String email, Long excludeId);
}
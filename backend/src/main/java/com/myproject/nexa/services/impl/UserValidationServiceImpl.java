package com.myproject.nexa.services.impl;

import com.myproject.nexa.dto.request.UserCreateRequest;
import com.myproject.nexa.dto.request.UserUpdateRequest;
import com.myproject.nexa.entities.User;
import com.myproject.nexa.exceptions.AppException;
import com.myproject.nexa.exceptions.ErrorCode;
import com.myproject.nexa.repositories.UserRepository;
import com.myproject.nexa.services.UserValidationService;
import com.myproject.nexa.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserValidationService {

    private static final Logger log = LoggerFactory.getLogger(UserValidationServiceImpl.class);

    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    @Override
    public void validateUserCreation(UserCreateRequest request) {
        log.debug("Validating user creation request");

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_002, "Username is required");
        }

        // Sanitize and validate username
        String sanitizedUsername = securityUtil.sanitizeUserInput(request.getUsername());
        if (!securityUtil.isValidUsername(sanitizedUsername)) {
            throw new AppException(ErrorCode.VALIDATION_002, "Username contains invalid characters or format");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_002, "Email is required");
        }

        // Validate email format
        if (!securityUtil.isValidEmail(request.getEmail())) {
            throw new AppException(ErrorCode.VALIDATION_003, "Invalid email format");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new AppException(ErrorCode.VALIDATION_004, "Password must be at least 6 characters");
        }
    }

    @Override
    public void validateUserUpdate(Long id, UserUpdateRequest request) {
        log.debug("Validating user update request for user ID: {}", id);
        if (id == null) {
            throw new AppException(ErrorCode.USER_005, "User ID is required");
        }

        if (request.getUsername() != null) {
            if (request.getUsername().trim().isEmpty()) {
                throw new AppException(ErrorCode.VALIDATION_002, "Username cannot be empty");
            }

            // Sanitize and validate username
            String sanitizedUsername = securityUtil.sanitizeUserInput(request.getUsername());
            if (!securityUtil.isValidUsername(sanitizedUsername)) {
                throw new AppException(ErrorCode.VALIDATION_002, "Username contains invalid characters or format");
            }
        }

        if (request.getEmail() != null) {
            if (request.getEmail().trim().isEmpty()) {
                throw new AppException(ErrorCode.VALIDATION_002, "Email cannot be empty");
            }

            // Validate email format
            if (!securityUtil.isValidEmail(request.getEmail())) {
                throw new AppException(ErrorCode.VALIDATION_003, "Invalid email format");
            }
        }
    }

    @Override
    public void validateUsernameUniqueness(String username, Long excludeId) {
        log.debug("Validating username uniqueness for: {} excluding ID: {}", username, excludeId);
        boolean exists = userRepository.existsByUsername(username);
        if (exists && excludeId != null) {
            // If we're updating, check if the existing user is not the one we're updating
            Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(excludeId)) {
                throw new AppException(ErrorCode.USER_002, "Username already exists");
            }
        } else if (exists && excludeId == null) {
            // If we're creating, just check if it exists
            throw new AppException(ErrorCode.USER_002, "Username already exists");
        }
    }

    @Override
    public void validateEmailUniqueness(String email, Long excludeId) {
        log.debug("Validating email uniqueness for: {} excluding ID: {}", email, excludeId);
        boolean exists = userRepository.existsByEmail(email);
        if (exists && excludeId != null) {
            // If we're updating, check if the existing user is not the one we're updating
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(excludeId)) {
                throw new AppException(ErrorCode.USER_003, "Email already exists");
            }
        } else if (exists && excludeId == null) {
            // If we're creating, just check if it exists
            throw new AppException(ErrorCode.USER_003, "Email already exists");
        }
    }
}
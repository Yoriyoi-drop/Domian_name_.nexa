package com.myproject.nexa.services;

import com.myproject.nexa.entities.User;
import com.myproject.nexa.repositories.UserRepository;
import com.myproject.nexa.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling password history to prevent reuse
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordHistoryService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    /**
     * Check if the new password has been used recently
     * @param userId The user ID
     * @param newPassword The new password to check
     * @return true if password is unique (not in recent history), false otherwise
     */
    public boolean isPasswordUnique(Long userId, String newPassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        // Get the password history for the user
        List<String> passwordHistory = user.getPasswordHistory();
        if (passwordHistory == null) {
            passwordHistory = new ArrayList<>();
        }

        // Check if the new password matches any in the history
        for (String encodedPassword : passwordHistory) {
            if (passwordEncoder.matches(newPassword, encodedPassword)) {
                log.warn("Password has been used recently by user: {}", userId);
                return false;
            }
        }

        return true;
    }

    /**
     * Add a new password to the user's history
     * @param user The user
     * @param newPassword The new password to add
     */
    public void addPasswordToHistory(User user, String newPassword) {
        if (user.getPasswordHistory() == null) {
            user.setPasswordHistory(new ArrayList<>());
        }

        // Encode the new password before storing in history
        String encodedPassword = passwordEncoder.encode(newPassword);
        
        // Limit history to last 5 passwords (configurable)
        if (user.getPasswordHistory().size() >= 5) {
            // Remove the oldest password if we've reached the limit
            user.getPasswordHistory().remove(0);
        }

        // Add the new password to history
        user.getPasswordHistory().add(encodedPassword);
        
        // Save the user with updated password history
        userRepository.save(user);
    }

    /**
     * Validate password complexity and check against history
     * @param userId The user ID
     * @param password The password to validate
     * @return List of validation errors, empty if valid
     */
    public List<String> validatePassword(Long userId, String password) {
        List<String> errors = new ArrayList<>();

        // Validate complexity requirements
        if (password == null || password.length() < 12) {
            errors.add("Password must be at least 12 characters long");
        }

        if (!password.matches(".*[A-Z].*")) {
            errors.add("Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*[a-z].*")) {
            errors.add("Password must contain at least one lowercase letter");
        }

        if (!password.matches(".*\\d.*")) {
            errors.add("Password must contain at least one number");
        }

        if (!password.matches(".*[@#$%^&+=!*()_\\-{}|:;<>?].*")) {
            errors.add("Password must contain at least one special character");
        }

        // Check if password is too common
        if (securityUtil.isPasswordTooCommon(password)) {
            errors.add("Password is too common and not allowed");
        }

        // Check password uniqueness against history
        if (userId != null && !isPasswordUnique(userId, password)) {
            errors.add("Password has been used recently and cannot be reused");
        }

        return errors;
    }
}
package com.myproject.nexa.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Utility class for security-related operations
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityUtil {

    private final InputSanitizer inputSanitizer;

    /**
     * Check if password contains common patterns that should be avoided
     */
    public boolean isPasswordTooCommon(String password) {
        if (password == null) {
            return false;
        }

        String lowerPassword = password.toLowerCase();

        // Common patterns to avoid
        String[] commonPatterns = {
            "password", "123456", "qwerty", "abc123", "password123",
            "admin", "user", "welcome", "letmein", "monkey",
            "123456789", "12345678", "12345", "1234", "1111",
            "0000", "9999", "trustno1", "dragon", "baseball",
            "football", "letmein", "monkey", "696969", "abc123",
            "mustang", "michael", "shadow", "master", "jennifer",
            "111111", "2000", "jordan", "superman", "harley",
            "1234567", "1234", "gulliver", "guitar", "buster",
            "marley", "booboo", "spider", "1234567890", "fantasy",
            "iloveyou", "michelle", "snoopy", "pepper", "liverpoo",
            "calvin", "123456a", "qwerty123", "123456789a", "987654321"
        };

        for (String pattern : commonPatterns) {
            if (lowerPassword.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Generate a secure random string for tokens
     */
    public String generateSecureRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Sanitize user input to prevent XSS and other injection attacks
     */
    public String sanitizeUserInput(String input) {
        if (input == null) {
            return null;
        }
        // Use the InputSanitizer for proper HTML sanitization
        return inputSanitizer.stripHtml(input);
    }

    /**
     * Validate username format
     */
    public boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        // Username should match alphanumeric and some special characters
        return username.matches("^[a-zA-Z0-9_]{3,50}$");
    }

    /**
     * Validate email format
     */
    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        // Basic email validation
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    /**
     * Validate a file name to prevent path traversal
     */
    public boolean isValidFileName(String fileName) {
        return inputSanitizer.sanitizeFilename(fileName).equals(fileName);
    }
}
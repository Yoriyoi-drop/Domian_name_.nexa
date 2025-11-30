package com.myproject.nexa.utils;

import com.myproject.nexa.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.passay.*;
import org.passay.dictionary.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for password validation and policy enforcement
 */
@Component
@RequiredArgsConstructor
public class PasswordValidator {

    private final AppProperties appProperties;

    /**
     * Validates a password against the security policy
     * @param password The password to validate
     * @return true if password meets requirements, false otherwise
     */
    public boolean validatePassword(String password) {
        if (password == null) {
            return false;
        }

        // Use Passay library for comprehensive password validation
        org.passay.PasswordValidator validator = buildPasswordValidator();
        RuleResult result = validator.validate(new PasswordData(password));

        return result.isValid();
    }

    /**
     * Generate password validation errors
     * @param password The password to validate
     * @return List of validation error messages
     */
    public List<String> getValidationErrors(String password) {
        if (password == null) {
            return Arrays.asList("Password cannot be null");
        }

        org.passay.PasswordValidator validator = buildPasswordValidator();
        RuleResult result = validator.validate(new PasswordData(password));

        if (result.isValid()) {
            return Arrays.asList(); // Return empty list if password is valid
        }

        return validator.getMessages(result).stream()
                .map(msg -> processMessage(msg))
                .collect(Collectors.toList());
    }

    /**
     * Builds the password validator with enterprise-grade requirements
     */
    private org.passay.PasswordValidator buildPasswordValidator() {
        // Create rules for strong passwords
        // Minimum 12 characters, uppercase, lowercase, number, special char
        return new org.passay.PasswordValidator(Arrays.asList(
            new LengthRule(12, 128), // At least 12 characters
            new CharacterRule(EnglishCharacterData.UpperCase, 1), // At least 1 uppercase
            new CharacterRule(EnglishCharacterData.LowerCase, 1), // At least 1 lowercase
            new CharacterRule(EnglishCharacterData.Digit, 1),     // At least 1 digit
            new CharacterRule(EnglishCharacterData.Special, 1),   // At least 1 special character
            new WhitespaceRule(), // No whitespace allowed
            new RepeatCharacterRegexRule(3), // No more than 3 repeated characters
            new UsernameRule(true, false) // Don't allow username in password
        ));
    }

    /**
     * Process and format validation messages
     */
    private String processMessage(String message) {
        // Customize the message formatting as needed
        return message.replaceAll("Password.*", "Password requirement not met: ");
    }

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
            "0000", "9999", "trustno1", "dragon", "baseball"
        };

        for (String pattern : commonPatterns) {
            if (lowerPassword.contains(pattern)) {
                return true;
            }
        }

        return false;
    }
}
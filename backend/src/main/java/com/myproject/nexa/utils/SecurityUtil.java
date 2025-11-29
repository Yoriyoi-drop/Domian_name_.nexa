package com.myproject.nexa.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Security utility for input sanitization and validation
 */
@Component
@Slf4j
public class SecurityUtil {

    /**
     * Sanitize user input to prevent XSS and injection attacks
     */
    public String sanitizeUserInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Basic XSS prevention - remove HTML tags and dangerous characters
        String sanitized = input.replaceAll("<[^>]*>", "")  // Remove HTML tags
                                .replaceAll("&[^;]*;", "")   // Remove HTML entities
                                .trim();
        
        return sanitized;
    }

    /**
     * Validate if username is valid
     */
    public boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        
        // Simple validation: alphanumeric and underscore, 3-50 chars
        return username.matches("^[a-zA-Z0-9_]{3,50}$");
    }

    /**
     * Validate if email is valid
     */
    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        // Basic email validation
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Check if input contains malicious content
     */
    public boolean containsMaliciousContent(String input) {
        if (input == null) {
            return false;
        }

        // Check for common SQL injection patterns
        String lowerInput = input.toLowerCase();
        String[] sqlPatterns = {"union", "select", "insert", "update", "delete", "drop", "exec", "execute", "--", "/*", "*/", "xp_", "sp_;"};
        for (String pattern : sqlPatterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }

        // Check for XSS patterns
        String[] xssPatterns = {"<script", "javascript:", "vbscript:", "onerror", "onload", "onmouseover", "onclick", "alert("};
        for (String pattern : xssPatterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }

        return false;
    }
}
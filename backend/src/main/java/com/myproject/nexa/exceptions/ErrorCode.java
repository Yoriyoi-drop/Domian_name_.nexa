package com.myproject.nexa.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Standardized error codes for the application.
 * Format: MODULE_CODE_ERROR_TYPE
 * Examples: AUTH_001, USER_002, VALIDATION_001
 */
@Getter
public enum ErrorCode {

    // Authentication errors
    AUTH_001(HttpStatus.UNAUTHORIZED, "AUTH_001", "Invalid credentials"),
    AUTH_002(HttpStatus.UNAUTHORIZED, "AUTH_002", "Access token expired"),
    AUTH_003(HttpStatus.UNAUTHORIZED, "AUTH_003", "Invalid access token"),
    AUTH_004(HttpStatus.FORBIDDEN, "AUTH_004", "Insufficient permissions"),
    AUTH_005(HttpStatus.UNAUTHORIZED, "AUTH_005", "User account is disabled"),
    AUTH_006(HttpStatus.UNAUTHORIZED, "AUTH_006", "User account is locked"),
    AUTH_007(HttpStatus.UNAUTHORIZED, "AUTH_007", "User account credentials expired"),
    AUTH_008(HttpStatus.UNAUTHORIZED, "AUTH_008", "User account is expired"),

    // User errors
    USER_001(HttpStatus.NOT_FOUND, "USER_001", "User not found"),
    USER_002(HttpStatus.CONFLICT, "USER_002", "User with this username already exists"),
    USER_003(HttpStatus.CONFLICT, "USER_003", "User with this email already exists"),
    USER_004(HttpStatus.BAD_REQUEST, "USER_004", "Invalid user data provided"),
    USER_005(HttpStatus.BAD_REQUEST, "USER_005", "User ID is required"),
    USER_006(HttpStatus.FORBIDDEN, "USER_006", "Cannot modify this user"),
    USER_007(HttpStatus.BAD_REQUEST, "USER_007", "User is already disabled"),
    USER_008(HttpStatus.BAD_REQUEST, "USER_008", "User is already enabled"),

    // Validation errors
    VALIDATION_001(HttpStatus.BAD_REQUEST, "VALIDATION_001", "Validation failed"),
    VALIDATION_002(HttpStatus.BAD_REQUEST, "VALIDATION_002", "Required field is missing"),
    VALIDATION_003(HttpStatus.BAD_REQUEST, "VALIDATION_003", "Invalid email format"),
    VALIDATION_004(HttpStatus.BAD_REQUEST, "VALIDATION_004", "Password too short"),
    VALIDATION_005(HttpStatus.BAD_REQUEST, "VALIDATION_005", "Invalid phone number format"),

    // Security errors
    SECURITY_001(HttpStatus.FORBIDDEN, "SECURITY_001", "Access denied"),
    SECURITY_002(HttpStatus.TOO_MANY_REQUESTS, "SECURITY_002", "Too many requests"),
    SECURITY_003(HttpStatus.FORBIDDEN, "SECURITY_003", "CSRF token invalid"),

    // Resource errors
    RESOURCE_001(HttpStatus.NOT_FOUND, "RESOURCE_001", "Resource not found"),
    RESOURCE_002(HttpStatus.CONFLICT, "RESOURCE_002", "Resource already exists"),
    RESOURCE_003(HttpStatus.GONE, "RESOURCE_003", "Resource has been deleted"),

    // Database errors
    DATABASE_001(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_001", "Database connection error"),
    DATABASE_002(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_002", "Database integrity violation"),
    DATABASE_003(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_003", "Database query error"),

    // System errors
    SYSTEM_001(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_001", "Internal server error"),
    SYSTEM_002(HttpStatus.SERVICE_UNAVAILABLE, "SYSTEM_002", "Service temporarily unavailable"),
    SYSTEM_003(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_003", "System configuration error"),

    // Business logic errors
    BUSINESS_001(HttpStatus.BAD_REQUEST, "BUSINESS_001", "Business rule violation"),
    BUSINESS_002(HttpStatus.BAD_REQUEST, "BUSINESS_002", "Operation not allowed in current state"),

    // Integration errors
    INTEGRATION_001(HttpStatus.BAD_GATEWAY, "INTEGRATION_001", "External service error"),
    INTEGRATION_002(HttpStatus.GATEWAY_TIMEOUT, "INTEGRATION_002", "External service timeout"),
    INTEGRATION_003(HttpStatus.SERVICE_UNAVAILABLE, "INTEGRATION_003", "External service unavailable");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
package com.myproject.nexa.exceptions;

public class BadRequestException extends AppException {
    public BadRequestException(String message) {
        super(ErrorCode.VALIDATION_001, message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(ErrorCode.VALIDATION_001, message, cause);
    }
}
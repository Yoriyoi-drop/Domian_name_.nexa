package com.myproject.nexa.exceptions;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(ErrorCode.AUTH_001, message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(ErrorCode.AUTH_001, message, cause);
    }
}
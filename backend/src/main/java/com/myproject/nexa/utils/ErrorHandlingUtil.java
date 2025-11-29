package com.myproject.nexa.utils;

import com.myproject.nexa.dto.response.ApiResponse;
import com.myproject.nexa.exceptions.AppException;
import com.myproject.nexa.exceptions.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility class for error handling and response building
 */
@Component
@Slf4j
public class ErrorHandlingUtil {

    /**
     * Create a structured error response from an exception
     */
    public static <T> ResponseEntity<ApiResponse<T>> createErrorResponse(Exception ex, Class<?> sourceClass) {
        if (ex instanceof AppException) {
            AppException appEx = (AppException) ex;
            log.error("AppException in {}: {} - Code: {}", 
                     sourceClass.getSimpleName(), appEx.getMessage(), appEx.getErrorCode().getCode(), ex);
            
            ApiResponse<T> errorResponse = ApiResponse.<T>builder()
                    .success(false)
                    .message(appEx.getMessage())
                    .errorCode(appEx.getErrorCode().getCode())
                    .build();
                    
            return ResponseEntity.status(appEx.getErrorCode().getHttpStatus()).body(errorResponse);
        } else {
            log.error("Unexpected exception in {}: {}", sourceClass.getSimpleName(), ex.getMessage(), ex);
            
            ApiResponse<T> errorResponse = ApiResponse.<T>builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .errorCode(ErrorCode.SYSTEM_001.getCode())
                    .build();
                    
            return ResponseEntity.status(ErrorCode.SYSTEM_001.getHttpStatus()).body(errorResponse);
        }
    }

    /**
     * Generate a unique trace ID for error tracking
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Log error with trace ID for debugging and monitoring
     */
    public static void logErrorWithTrace(String traceId, String message, Throwable ex, Object... params) {
        String formattedMessage = String.format(message, params);
        log.error("[TRACE_ID: {}] {}", traceId, formattedMessage, ex);
    }

    /**
     * Log business error (not technical error) for monitoring
     */
    public static void logBusinessError(String errorCode, String message, Object... params) {
        String formattedMessage = String.format("Business Error [CODE: %s] - %s", errorCode, String.format(message, params));
        log.warn(formattedMessage);
    }
}
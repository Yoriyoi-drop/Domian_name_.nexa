package com.myproject.nexa.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myproject.nexa.exceptions.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized error response DTO with error codes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String error;
    private int status;
    private String code;
    private List<String> details;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String path;
    private String traceId;

    /**
     * Create error response from error code
     */
    public static ErrorResponse fromErrorCode(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
                .message(errorCode.getMessage())
                .error(errorCode.name())
                .status(errorCode.getHttpStatus().value())
                .code(errorCode.getCode())
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    /**
     * Create error response from error code with additional details
     */
    public static ErrorResponse fromErrorCode(ErrorCode errorCode, String path, List<String> details) {
        return ErrorResponse.builder()
                .message(errorCode.getMessage())
                .error(errorCode.name())
                .status(errorCode.getHttpStatus().value())
                .code(errorCode.getCode())
                .details(details)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
}
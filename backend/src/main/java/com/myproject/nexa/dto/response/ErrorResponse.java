package com.myproject.nexa.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myproject.nexa.exceptions.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String requestId;
    private String message;
    private String errorCode;
    private String errorType;
    private List<String> details;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String path;

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
            .requestId(UUID.randomUUID().toString())
            .message(errorCode.getMessage())
            .errorCode(errorCode.getCode())
            .errorType(errorCode.name())
            .timestamp(LocalDateTime.now())
            .path(path)
            .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String message, String path) {
        return ErrorResponse.builder()
            .requestId(UUID.randomUUID().toString())
            .message(message)
            .errorCode(errorCode.getCode())
            .errorType(errorCode.name())
            .timestamp(LocalDateTime.now())
            .path(path)
            .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String message, List<String> details, String path) {
        return ErrorResponse.builder()
            .requestId(UUID.randomUUID().toString())
            .message(message)
            .errorCode(errorCode.getCode())
            .errorType(errorCode.name())
            .details(details)
            .timestamp(LocalDateTime.now())
            .path(path)
            .build();
    }
}
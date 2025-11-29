package com.myproject.nexa.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myproject.nexa.exceptions.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private String errorCode;
    private String timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .timestamp(LocalDateTime.now().toString())
            .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now().toString())
            .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .timestamp(LocalDateTime.now().toString())
            .build();
    }

    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .errors(errors)
            .timestamp(LocalDateTime.now().toString())
            .build();
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now().toString())
            .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(errorCode.getMessage())
            .errorCode(errorCode.getCode())
            .timestamp(LocalDateTime.now().toString())
            .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, List<String> errors) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(errorCode.getMessage())
            .errorCode(errorCode.getCode())
            .errors(errors)
            .timestamp(LocalDateTime.now().toString())
            .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, T data) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(errorCode.getMessage())
            .errorCode(errorCode.getCode())
            .data(data)
            .timestamp(LocalDateTime.now().toString())
            .build();
    }
}
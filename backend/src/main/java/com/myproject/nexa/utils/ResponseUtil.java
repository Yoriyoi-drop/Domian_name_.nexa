package com.myproject.nexa.utils;

import com.myproject.nexa.dto.response.ApiResponse;

public class ResponseUtil {

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.success(data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.success(message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.error(message);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.error(message, data);
    }
}
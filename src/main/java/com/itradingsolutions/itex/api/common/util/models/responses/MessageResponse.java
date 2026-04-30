package com.itradingsolutions.itex.api.common.util.models.responses;


public record MessageResponse<T>(
        String title,
        String message,
        T data
) {
}

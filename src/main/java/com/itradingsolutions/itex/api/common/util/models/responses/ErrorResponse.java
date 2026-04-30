package com.itradingsolutions.itex.api.common.util.models.responses;

import java.util.Map;

public record ErrorResponse(
        String errorMessage,
        Integer statusCode,
        Map<String, String> formErrors
) {
}

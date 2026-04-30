package com.itradingsolutions.itex.api.ip.qr.models.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record IpQuoteRequestOtherChargeRequest(
        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Value is required")
        @Min(0)
        BigDecimal value
) {
}

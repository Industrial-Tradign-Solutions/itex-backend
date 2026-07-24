package com.itradingsolutions.itex.api.ip.po.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record IpPurchaseOrderOtherChargeRequest(
        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Value is required")
        @Min(0)
        BigDecimal value
) {
}

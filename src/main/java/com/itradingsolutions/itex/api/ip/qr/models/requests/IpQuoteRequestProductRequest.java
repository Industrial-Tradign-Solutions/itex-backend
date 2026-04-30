package com.itradingsolutions.itex.api.ip.qr.models.requests;

import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.models.enums.UnitType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record IpQuoteRequestProductRequest(

        @NotNull(message = "Product is required")
        UUID productId,

        @NotNull(message = "Quantity is required")
        @Min(0)
        BigDecimal quantity,

        @NotNull(message = "Unit Type is required")
        UnitType unitType,

        Integer leadTime,
        LeadTime leadTimeType,
        BigDecimal unitPrice
) {
}

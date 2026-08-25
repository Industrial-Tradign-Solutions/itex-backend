package com.itradingsolutions.itex.api.sales.invoices.models.request;

import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.ip.products.models.enums.ProductCondition;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Body of {@code POST}/{@code PUT /sales/invoice/{id}/product}. {@code unitPrice} is the raw cost
 * and {@code profitMargin} the margin as a direct percentage (10.00 = 10%); the billed price is
 * recomputed as {@code cost * (1 + margin / 100)} so the margin is never applied twice (see
 * {@code InvoiceProductDTO.getSellingExtendedPrice}).
 */
public record InvoiceProductRequest(

        @NotNull(message = "Product is required")
        UUID productId,

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0", message = "Quantity cannot be negative")
        BigDecimal quantity,

        @NotBlank(message = "Unit type is required")
        @Size(max = 50, message = "The unit type cannot exceed 50 characters")
        String unitType,

        Integer leadTime,

        LeadTime leadTimeType,

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0", message = "Unit price cannot be negative")
        BigDecimal unitPrice,

        @NotNull(message = "Profit margin is required")
        @DecimalMin(value = "0.01", message = "Profit margin must be at least 0.01")
        @DecimalMax(value = "100", message = "Profit margin cannot exceed 100")
        BigDecimal profitMargin,

        @NotNull(message = "Condition is required")
        ProductCondition condition
) {}

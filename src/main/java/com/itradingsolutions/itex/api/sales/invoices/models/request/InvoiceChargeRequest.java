package com.itradingsolutions.itex.api.sales.invoices.models.request;

import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceChargeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Body of {@code POST}/{@code PUT /sales/invoice/{id}/charge}. {@code value} may be negative to
 * support {@code DISCOUNT} charges.
 */
public record InvoiceChargeRequest(

        @NotBlank(message = "Description is required")
        @Size(max = 100, message = "The description cannot exceed 100 characters")
        String description,

        @NotNull(message = "Charge type is required")
        InvoiceChargeType type,

        @NotNull(message = "Value is required")
        BigDecimal value
) {}

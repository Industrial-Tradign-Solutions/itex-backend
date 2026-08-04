package com.itradingsolutions.itex.api.sales.invoices.models.request;

import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceTaxType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Body of {@code POST}/{@code PUT /sales/invoice/{id}/tax}. Taxes are entered manually: the frontend
 * pre-loads the subtotal for convenience, but {@code rate}, {@code taxableBase} and {@code value}
 * arrive already computed and are persisted as received — the backend does not recompute them nor
 * couple them to the products subtotal.
 */
public record InvoiceTaxRequest(

        @NotNull(message = "Tax type is required")
        InvoiceTaxType type,

        @NotBlank(message = "Description is required")
        @Size(max = 100, message = "The description cannot exceed 100 characters")
        String description,

        @NotNull(message = "Rate is required")
        @DecimalMin(value = "0", message = "Rate cannot be negative")
        BigDecimal rate,

        @NotNull(message = "Taxable base is required")
        @DecimalMin(value = "0", message = "Taxable base cannot be negative")
        BigDecimal taxableBase,

        @NotNull(message = "Value is required")
        @DecimalMin(value = "0", message = "Value cannot be negative")
        BigDecimal value
) {}

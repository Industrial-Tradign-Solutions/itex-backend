package com.itradingsolutions.itex.api.sales.invoices.models.response;

import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceTaxType;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceTaxResponse(
        UUID id,
        InvoiceTaxType type,
        String description,
        BigDecimal rate,
        BigDecimal taxableBase,
        BigDecimal value
) {
}

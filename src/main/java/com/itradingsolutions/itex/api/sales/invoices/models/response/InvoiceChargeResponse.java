package com.itradingsolutions.itex.api.sales.invoices.models.response;

import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceChargeType;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceChargeResponse(
        UUID id,
        String description,
        InvoiceChargeType type,
        BigDecimal value
) {
}

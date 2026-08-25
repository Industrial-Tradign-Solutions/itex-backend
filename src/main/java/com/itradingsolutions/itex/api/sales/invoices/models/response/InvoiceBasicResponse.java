package com.itradingsolutions.itex.api.sales.invoices.models.response;

import java.util.UUID;

public record InvoiceBasicResponse(
        UUID id,
        String number
) {
}

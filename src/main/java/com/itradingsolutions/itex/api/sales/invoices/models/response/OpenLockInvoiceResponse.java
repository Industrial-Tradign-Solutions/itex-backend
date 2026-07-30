package com.itradingsolutions.itex.api.sales.invoices.models.response;

public record OpenLockInvoiceResponse(
        InvoiceResponse data,
        boolean isValidOpen
) {
}

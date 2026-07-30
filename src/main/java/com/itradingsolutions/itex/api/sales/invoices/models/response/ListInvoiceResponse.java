package com.itradingsolutions.itex.api.sales.invoices.models.response;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record ListInvoiceResponse(
        UUID id,
        String draftNumber,
        String number,
        String name,
        ClientResponse client,
        BasicUserResponse salesRep,
        InvoiceStatus status,
        Currency currency,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal balanceDue,
        ZonedDateTime dueAt,
        boolean overdue,
        ZonedDateTime createdAt
) {
}

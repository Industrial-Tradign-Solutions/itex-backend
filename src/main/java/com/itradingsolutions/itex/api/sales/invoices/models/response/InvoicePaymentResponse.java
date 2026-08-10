package com.itradingsolutions.itex.api.sales.invoices.models.response;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

public record InvoicePaymentResponse(
        UUID id,
        BigDecimal amount,
        LocalDate paymentDate,
        PaymentMethod paymentMethod,
        String receiptOriginalName,
        String notes,
        boolean voided,
        String voidedReason,
        ZonedDateTime voidedAt,
        BasicUserResponse voidedBy,
        BasicUserResponse registeredBy,
        ZonedDateTime createdAt
) {
}

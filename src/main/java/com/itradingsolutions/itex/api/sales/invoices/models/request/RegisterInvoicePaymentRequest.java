package com.itradingsolutions.itex.api.sales.invoices.models.request;

import com.itradingsolutions.itex.api.common.util.models.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JSON part of {@code POST /sales/invoice/{id}/payment} (multipart, alongside the receipt file).
 * {@code amount} is this instalment alone, not the running total; the server rejects anything above
 * the outstanding balance, so overpayments never produce a negative balance (invoicing guide §7).
 */
public record RegisterInvoicePaymentRequest(

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.00001", message = "The amount must be greater than zero")
        @Digits(integer = 10, fraction = 5, message = "The amount cannot have more than 5 decimals")
        BigDecimal amount,

        @NotNull(message = "Payment date is required")
        LocalDate paymentDate,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @Size(max = 1000, message = "The notes cannot exceed 1000 characters")
        String notes
) {}

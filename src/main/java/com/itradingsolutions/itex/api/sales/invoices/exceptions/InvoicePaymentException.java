package com.itradingsolutions.itex.api.sales.invoices.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

public class InvoicePaymentException extends BadRequestException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvoicePaymentException(String message) {
        super(message);
    }
}

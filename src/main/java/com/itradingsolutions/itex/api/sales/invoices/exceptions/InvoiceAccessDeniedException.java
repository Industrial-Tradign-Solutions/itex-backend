package com.itradingsolutions.itex.api.sales.invoices.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

public class InvoiceAccessDeniedException extends BadRequestException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InvoiceAccessDeniedException(String message) {
        super(message);
    }
}

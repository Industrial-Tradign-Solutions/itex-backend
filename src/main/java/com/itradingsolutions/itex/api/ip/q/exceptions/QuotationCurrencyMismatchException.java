package com.itradingsolutions.itex.api.ip.q.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

/**
 * Exception thrown when attempting to create or modify a Quotation with Quote Requests
 * that have a different currency than the Quotation's currency.
 */
public class QuotationCurrencyMismatchException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = 7823456789012345678L;

    public QuotationCurrencyMismatchException(String message) {
        super(message);
    }
}

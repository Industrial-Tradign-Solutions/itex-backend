package com.itradingsolutions.itex.api.ip.q.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

/**
 * Exception thrown when attempting to create or modify a Quotation with Quote Requests
 * that belong to a different client than the Quotation's client.
 */
public class QuotationClientMismatchException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = 8945231567823456789L;

    public QuotationClientMismatchException(String message) {
        super(message);
    }
}

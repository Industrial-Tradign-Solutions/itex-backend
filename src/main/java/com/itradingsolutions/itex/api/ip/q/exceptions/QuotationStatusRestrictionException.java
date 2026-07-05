package com.itradingsolutions.itex.api.ip.q.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

/**
 * Exception thrown when attempting an operation that is not allowed
 * in the current Quotation status.
 */
public class QuotationStatusRestrictionException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = 8945231567823456790L;

    public QuotationStatusRestrictionException(String message) {
        super(message);
    }
}

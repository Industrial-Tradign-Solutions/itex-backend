package com.itradingsolutions.itex.api.ip.q.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

/**
 * Exception thrown when attempting to add a Quote Request that is already linked
 * to the Quotation.
 */
public class QuoteRequestAlreadyLinkedException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = 3456789012345678901L;

    public QuoteRequestAlreadyLinkedException(String message) {
        super(message);
    }
}

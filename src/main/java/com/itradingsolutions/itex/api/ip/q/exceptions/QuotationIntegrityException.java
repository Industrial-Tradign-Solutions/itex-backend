package com.itradingsolutions.itex.api.ip.q.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;
import java.util.List;

public class QuotationIntegrityException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = 9823456789012345L;

    private final List<String> errors;

    public QuotationIntegrityException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
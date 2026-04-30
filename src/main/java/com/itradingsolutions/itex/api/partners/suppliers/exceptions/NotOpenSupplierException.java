package com.itradingsolutions.itex.api.partners.suppliers.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotOpenSupplierException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -3342008794566035566L;

    public NotOpenSupplierException() {
        super();
    }

    public NotOpenSupplierException(String message) {
        super(message);
    }

    public NotOpenSupplierException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotOpenSupplierException(Throwable cause) {
        super(cause);
    }

    protected NotOpenSupplierException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

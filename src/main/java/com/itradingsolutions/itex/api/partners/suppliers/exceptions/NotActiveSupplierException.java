package com.itradingsolutions.itex.api.partners.suppliers.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotActiveSupplierException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -3342008794566035566L;

    public NotActiveSupplierException() {
        super();
    }

    public NotActiveSupplierException(String message) {
        super(message);
    }

    public NotActiveSupplierException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotActiveSupplierException(Throwable cause) {
        super(cause);
    }

    protected NotActiveSupplierException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

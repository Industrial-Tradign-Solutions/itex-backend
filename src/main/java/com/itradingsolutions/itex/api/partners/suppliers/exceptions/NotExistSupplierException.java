package com.itradingsolutions.itex.api.partners.suppliers.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistSupplierException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -3342008794566035566L;

    public NotExistSupplierException() {
        super();
    }

    public NotExistSupplierException(String message) {
        super(message);
    }

    public NotExistSupplierException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistSupplierException(Throwable cause) {
        super(cause);
    }

    protected NotExistSupplierException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

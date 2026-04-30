package com.itradingsolutions.itex.api.partners.suppliers.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistSupplierContactException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -3342008794566035566L;

    public NotExistSupplierContactException() {
        super();
    }

    public NotExistSupplierContactException(String message) {
        super(message);
    }

    public NotExistSupplierContactException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistSupplierContactException(Throwable cause) {
        super(cause);
    }

    protected NotExistSupplierContactException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

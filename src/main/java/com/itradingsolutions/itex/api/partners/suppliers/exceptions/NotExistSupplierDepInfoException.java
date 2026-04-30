package com.itradingsolutions.itex.api.partners.suppliers.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistSupplierDepInfoException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -3342008794566035566L;

    public NotExistSupplierDepInfoException() {
        super();
    }

    public NotExistSupplierDepInfoException(String message) {
        super(message);
    }

    public NotExistSupplierDepInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistSupplierDepInfoException(Throwable cause) {
        super(cause);
    }

    protected NotExistSupplierDepInfoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

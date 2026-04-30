package com.itradingsolutions.itex.api.partners.suppliers.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistSupplierContactPhoneException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -3342008794566035566L;

    public NotExistSupplierContactPhoneException() {
        super();
    }

    public NotExistSupplierContactPhoneException(String message) {
        super(message);
    }

    public NotExistSupplierContactPhoneException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistSupplierContactPhoneException(Throwable cause) {
        super(cause);
    }

    protected NotExistSupplierContactPhoneException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

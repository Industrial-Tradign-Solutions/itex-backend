package com.itradingsolutions.itex.api.ip.products.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class ProductStatusChangeException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -3968209933481048847L;

    public ProductStatusChangeException() {
        super();
    }

    public ProductStatusChangeException(String message) {
        super(message);
    }

    public ProductStatusChangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductStatusChangeException(Throwable cause) {
        super(cause);
    }

    protected ProductStatusChangeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
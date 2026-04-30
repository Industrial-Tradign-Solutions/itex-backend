package com.itradingsolutions.itex.api.ip.products.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotOpenIpProductException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -9158326929715126952L;

    public NotOpenIpProductException() {
        super();
    }

    protected NotOpenIpProductException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NotOpenIpProductException(Throwable cause) {
        super(cause);
    }

    public NotOpenIpProductException(String message) {
        super(message);
    }

    public NotOpenIpProductException(String message, Throwable cause) {
        super(message, cause);
    }
}

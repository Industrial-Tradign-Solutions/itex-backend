package com.itradingsolutions.itex.api.ip.products.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistIpProductException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -285089478930385423L;

    public NotExistIpProductException() {
        super();
    }

    public NotExistIpProductException(String message) {
        super(message);
    }

    public NotExistIpProductException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistIpProductException(Throwable cause) {
        super(cause);
    }

    protected NotExistIpProductException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

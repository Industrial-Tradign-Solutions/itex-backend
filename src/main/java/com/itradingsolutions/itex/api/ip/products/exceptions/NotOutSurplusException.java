package com.itradingsolutions.itex.api.ip.products.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotOutSurplusException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 2125651018993743588L;

    public NotOutSurplusException() {
        super();
    }

    public NotOutSurplusException(String message) {
        super(message);
    }

    public NotOutSurplusException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotOutSurplusException(Throwable cause) {
        super(cause);
    }

    protected NotOutSurplusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

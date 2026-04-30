package com.itradingsolutions.itex.api.ip.qr.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotChangeStatusException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 4848804563594528182L;

    public NotChangeStatusException() {
        super();
    }

    public NotChangeStatusException(String message) {
        super(message);
    }

    public NotChangeStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotChangeStatusException(Throwable cause) {
        super(cause);
    }

    protected NotChangeStatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.itradingsolutions.itex.api.partners.clients.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotActiveClientException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -3342008794566035566L;

    public NotActiveClientException() {
        super();
    }

    public NotActiveClientException(String message) {
        super(message);
    }

    public NotActiveClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotActiveClientException(Throwable cause) {
        super(cause);
    }

    protected NotActiveClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

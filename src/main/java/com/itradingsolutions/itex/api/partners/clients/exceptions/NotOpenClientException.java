package com.itradingsolutions.itex.api.partners.clients.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotOpenClientException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -3342008794566035566L;

    public NotOpenClientException() {
        super();
    }

    public NotOpenClientException(String message) {
        super(message);
    }

    public NotOpenClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotOpenClientException(Throwable cause) {
        super(cause);
    }

    protected NotOpenClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

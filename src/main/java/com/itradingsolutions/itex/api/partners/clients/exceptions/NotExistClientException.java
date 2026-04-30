package com.itradingsolutions.itex.api.partners.clients.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistClientException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -3342008794566035566L;

    public NotExistClientException() {
        super();
    }

    public NotExistClientException(String message) {
        super(message);
    }

    public NotExistClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistClientException(Throwable cause) {
        super(cause);
    }

    protected NotExistClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

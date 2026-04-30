package com.itradingsolutions.itex.api.partners.clients.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistClientContactException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -3342008794566035566L;

    public NotExistClientContactException() {
        super();
    }

    public NotExistClientContactException(String message) {
        super(message);
    }

    public NotExistClientContactException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistClientContactException(Throwable cause) {
        super(cause);
    }

    protected NotExistClientContactException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

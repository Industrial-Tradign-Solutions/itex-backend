package com.itradingsolutions.itex.api.partners.clients.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistClientDepInfoException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -3342008794566035566L;

    public NotExistClientDepInfoException() {
        super();
    }

    public NotExistClientDepInfoException(String message) {
        super(message);
    }

    public NotExistClientDepInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistClientDepInfoException(Throwable cause) {
        super(cause);
    }

    protected NotExistClientDepInfoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

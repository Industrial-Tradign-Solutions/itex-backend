package com.itradingsolutions.itex.api.admin.user.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotEqualPasswordException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 8195723146767585305L;

    public NotEqualPasswordException() {
        super();
    }

    public NotEqualPasswordException(String message) {
        super(message);
    }

    public NotEqualPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEqualPasswordException(Throwable cause) {
        super(cause);
    }

    protected NotEqualPasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

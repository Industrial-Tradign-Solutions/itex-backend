package com.itradingsolutions.itex.api.admin.role.exceptions;

import java.io.Serial;

public class ActionNotAccessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3016290489609606146L;

    public ActionNotAccessException() {
        super();
    }

    public ActionNotAccessException(String message) {
        super(message);
    }

    public ActionNotAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionNotAccessException(Throwable cause) {
        super(cause);
    }

    protected ActionNotAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

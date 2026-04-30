package com.itradingsolutions.itex.api.admin.user.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotUserActiveException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = -251235544756654784L;

    public NotUserActiveException() {
        super();
    }

    public NotUserActiveException(String message) {
        super(message);
    }

    public NotUserActiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotUserActiveException(Throwable cause) {
        super(cause);
    }

    protected NotUserActiveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

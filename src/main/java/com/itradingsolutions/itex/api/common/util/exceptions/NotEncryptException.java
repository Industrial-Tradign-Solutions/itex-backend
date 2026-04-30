package com.itradingsolutions.itex.api.common.util.exceptions;

import java.io.Serial;

public class NotEncryptException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -1166644657730126205L;

    public NotEncryptException() {
        super();
    }

    public NotEncryptException(String message) {
        super(message);
    }

    public NotEncryptException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEncryptException(Throwable cause) {
        super(cause);
    }

    protected NotEncryptException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

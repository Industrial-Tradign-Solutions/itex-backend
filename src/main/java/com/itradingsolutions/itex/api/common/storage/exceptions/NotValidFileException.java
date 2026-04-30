package com.itradingsolutions.itex.api.common.storage.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

public class NotValidFileException extends NotFoundException {
    public NotValidFileException() {
        super();
    }

    public NotValidFileException(Throwable cause) {
        super(cause);
    }

    public NotValidFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotValidFileException(String message) {
        super(message);
    }

    protected NotValidFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

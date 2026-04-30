package com.itradingsolutions.itex.api.common.storage.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

public class SaveFileException extends NotFoundException {

    public SaveFileException() {
        super();
    }

    public SaveFileException(String message) {
        super(message);
    }

    public SaveFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveFileException(Throwable cause) {
        super(cause);
    }

    protected SaveFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

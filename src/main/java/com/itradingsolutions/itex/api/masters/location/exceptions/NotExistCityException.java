package com.itradingsolutions.itex.api.masters.location.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistCityException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 5106064027094834324L;

    public NotExistCityException() {
        super();
    }

    public NotExistCityException(String message) {
        super(message);
    }

    public NotExistCityException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistCityException(Throwable cause) {
        super(cause);
    }

    protected NotExistCityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.itradingsolutions.itex.api.masters.location.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistCountryException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -1960835772905967516L;

    public NotExistCountryException() {
        super();
    }

    public NotExistCountryException(String message) {
        super(message);
    }

    public NotExistCountryException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistCountryException(Throwable cause) {
        super(cause);
    }

    protected NotExistCountryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

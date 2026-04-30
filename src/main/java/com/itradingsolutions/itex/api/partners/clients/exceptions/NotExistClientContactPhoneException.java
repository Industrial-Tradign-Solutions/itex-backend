package com.itradingsolutions.itex.api.partners.clients.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistClientContactPhoneException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -3342008794566035566L;

    public NotExistClientContactPhoneException() {
        super();
    }

    public NotExistClientContactPhoneException(String message) {
        super(message);
    }

    public NotExistClientContactPhoneException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistClientContactPhoneException(Throwable cause) {
        super(cause);
    }

    protected NotExistClientContactPhoneException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

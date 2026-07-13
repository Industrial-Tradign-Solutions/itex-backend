package com.itradingsolutions.itex.api.ip.po.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotOpenPoException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 1829365748123746512L;

    public NotOpenPoException() {
        super();
    }

    public NotOpenPoException(String message) {
        super(message);
    }

    public NotOpenPoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotOpenPoException(Throwable cause) {
        super(cause);
    }

    protected NotOpenPoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

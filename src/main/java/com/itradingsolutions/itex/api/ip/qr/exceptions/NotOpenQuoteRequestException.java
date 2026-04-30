package com.itradingsolutions.itex.api.ip.qr.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotOpenQuoteRequestException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 5393009500575105009L;

    public NotOpenQuoteRequestException() {
        super();
    }

    public NotOpenQuoteRequestException(String message) {
        super(message);
    }

    public NotOpenQuoteRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotOpenQuoteRequestException(Throwable cause) {
        super(cause);
    }

    protected NotOpenQuoteRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

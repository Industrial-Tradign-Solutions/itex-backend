package com.itradingsolutions.itex.api.ip.qr.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistIpQuoteRequestException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 1416396132856563676L;

    public NotExistIpQuoteRequestException() {
        super();
    }

    public NotExistIpQuoteRequestException(String message) {
        super(message);
    }

    public NotExistIpQuoteRequestException(Throwable cause) {
        super(cause);
    }

    public NotExistIpQuoteRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    protected NotExistIpQuoteRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.itradingsolutions.itex.api.ip.q.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistIpQuotationException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 2144819287583084732L;

    public NotExistIpQuotationException() {
        super();
    }

    public NotExistIpQuotationException(String message) {
        super(message);
    }

    public NotExistIpQuotationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistIpQuotationException(Throwable cause) {
        super(cause);
    }

    protected NotExistIpQuotationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

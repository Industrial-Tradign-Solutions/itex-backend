package com.itradingsolutions.itex.api.ip.products.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotReplaceIpProductException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -417587994414649855L;

    public NotReplaceIpProductException() {
        super();
    }

    public NotReplaceIpProductException(String message) {
        super(message);
    }

    public NotReplaceIpProductException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotReplaceIpProductException(Throwable cause) {
        super(cause);
    }

    protected NotReplaceIpProductException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

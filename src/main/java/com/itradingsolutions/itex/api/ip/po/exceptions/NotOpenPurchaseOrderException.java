package com.itradingsolutions.itex.api.ip.po.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotOpenPurchaseOrderException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 6224501842188475892L;

    public NotOpenPurchaseOrderException() {
        super();
    }

    public NotOpenPurchaseOrderException(String message) {
        super(message);
    }

    public NotOpenPurchaseOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotOpenPurchaseOrderException(Throwable cause) {
        super(cause);
    }

    protected NotOpenPurchaseOrderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

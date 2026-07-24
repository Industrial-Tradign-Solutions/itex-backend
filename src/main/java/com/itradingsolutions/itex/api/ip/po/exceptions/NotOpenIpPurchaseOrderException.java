package com.itradingsolutions.itex.api.ip.po.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotOpenIpPurchaseOrderException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 6224501842188475892L;

    public NotOpenIpPurchaseOrderException() {
        super();
    }

    public NotOpenIpPurchaseOrderException(String message) {
        super(message);
    }

    public NotOpenIpPurchaseOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotOpenIpPurchaseOrderException(Throwable cause) {
        super(cause);
    }

    protected NotOpenIpPurchaseOrderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

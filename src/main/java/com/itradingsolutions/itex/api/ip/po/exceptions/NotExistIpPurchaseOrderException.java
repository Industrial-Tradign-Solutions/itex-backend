package com.itradingsolutions.itex.api.ip.po.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistIpPurchaseOrderException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 7140651974870253507L;

    public NotExistIpPurchaseOrderException() {
        super();
    }

    public NotExistIpPurchaseOrderException(String message) {
        super(message);
    }

    public NotExistIpPurchaseOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistIpPurchaseOrderException(Throwable cause) {
        super(cause);
    }

    protected NotExistIpPurchaseOrderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

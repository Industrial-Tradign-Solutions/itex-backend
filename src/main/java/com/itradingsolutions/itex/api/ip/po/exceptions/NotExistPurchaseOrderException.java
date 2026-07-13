package com.itradingsolutions.itex.api.ip.po.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistPurchaseOrderException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 7140651974870253507L;

    public NotExistPurchaseOrderException() {
        super();
    }

    public NotExistPurchaseOrderException(String message) {
        super(message);
    }

    public NotExistPurchaseOrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistPurchaseOrderException(Throwable cause) {
        super(cause);
    }

    protected NotExistPurchaseOrderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

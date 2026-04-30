package com.itradingsolutions.itex.api.ip.qr.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class QrOtherChargeExistException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 8674482801885684736L;

    public QrOtherChargeExistException() {
        super();
    }

    public QrOtherChargeExistException(String message) {
        super(message);
    }

    public QrOtherChargeExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public QrOtherChargeExistException(Throwable cause) {
        super(cause);
    }

    protected QrOtherChargeExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.itradingsolutions.itex.api.ip.po.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotOpenIpPoException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 1829365748123746512L;

    public NotOpenIpPoException() {
        super();
    }

    public NotOpenIpPoException(String message) {
        super(message);
    }

    public NotOpenIpPoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotOpenIpPoException(Throwable cause) {
        super(cause);
    }

    protected NotOpenIpPoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

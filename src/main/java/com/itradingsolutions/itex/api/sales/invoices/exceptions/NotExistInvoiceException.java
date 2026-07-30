package com.itradingsolutions.itex.api.sales.invoices.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistInvoiceException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NotExistInvoiceException() {
        super();
    }

    public NotExistInvoiceException(String message) {
        super(message);
    }

    public NotExistInvoiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistInvoiceException(Throwable cause) {
        super(cause);
    }

    protected NotExistInvoiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

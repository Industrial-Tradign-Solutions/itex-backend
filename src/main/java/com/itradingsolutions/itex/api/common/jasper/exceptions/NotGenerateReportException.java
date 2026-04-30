package com.itradingsolutions.itex.api.common.jasper.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotGenerateReportException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 4225613122582839901L;

    public NotGenerateReportException() {
        super();
    }

    public NotGenerateReportException(String message) {
        super(message);
    }

    public NotGenerateReportException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotGenerateReportException(Throwable cause) {
        super(cause);
    }

    protected NotGenerateReportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

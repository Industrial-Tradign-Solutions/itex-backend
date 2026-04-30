package com.itradingsolutions.itex.api.admin.role.exceptions;

import java.io.Serial;

public class ModuleNotAccessException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 7521986836431907L;

    public ModuleNotAccessException() {
        super();
    }

    public ModuleNotAccessException(String message) {
        super(message);
    }

    public ModuleNotAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleNotAccessException(Throwable cause) {
        super(cause);
    }

    protected ModuleNotAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

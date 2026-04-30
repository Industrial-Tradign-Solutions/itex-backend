package com.itradingsolutions.itex.api.admin.role.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistRoleException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -5319345486515277867L;

    public NotExistRoleException() {
        super();
    }

    public NotExistRoleException(String message) {
        super(message);
    }

    public NotExistRoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistRoleException(Throwable cause) {
        super(cause);
    }

    protected NotExistRoleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

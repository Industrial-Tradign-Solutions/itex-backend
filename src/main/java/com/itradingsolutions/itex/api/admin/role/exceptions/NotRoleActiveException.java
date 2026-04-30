package com.itradingsolutions.itex.api.admin.role.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotRoleActiveException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 5080768910548478506L;

    public NotRoleActiveException() {
        super();
    }

    public NotRoleActiveException(String message) {
        super(message);
    }

    public NotRoleActiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotRoleActiveException(Throwable cause) {
        super(cause);
    }

    protected NotRoleActiveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

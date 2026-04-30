package com.itradingsolutions.itex.api.masters.department.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotDepartmentActiveException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 7302621846658794206L;

    public NotDepartmentActiveException() {
        super();
    }

    public NotDepartmentActiveException(String message) {
        super(message);
    }

    public NotDepartmentActiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotDepartmentActiveException(Throwable cause) {
        super(cause);
    }

    protected NotDepartmentActiveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

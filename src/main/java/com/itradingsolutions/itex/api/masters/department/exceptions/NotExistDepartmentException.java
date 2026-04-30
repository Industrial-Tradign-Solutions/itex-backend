package com.itradingsolutions.itex.api.masters.department.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistDepartmentException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -1960835772905967516L;

    public NotExistDepartmentException() {
        super();
    }

    public NotExistDepartmentException(String message) {
        super(message);
    }

    public NotExistDepartmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistDepartmentException(Throwable cause) {
        super(cause);
    }

    protected NotExistDepartmentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

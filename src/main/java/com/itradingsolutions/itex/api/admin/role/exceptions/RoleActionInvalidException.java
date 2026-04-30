package com.itradingsolutions.itex.api.admin.role.exceptions;

import java.io.Serial;

public class RoleActionInvalidException extends IllegalArgumentException {
    @Serial
    private static final long serialVersionUID = 5602938627080881280L;

    public RoleActionInvalidException(String message) {
        super(message);
    }
}

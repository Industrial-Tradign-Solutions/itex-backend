package com.itradingsolutions.itex.api.admin.role.exceptions;

import java.io.Serial;

public class RoleMenuEmptyListException extends IllegalArgumentException {
    @Serial
    private static final long serialVersionUID = 5557594137739817602L;

    public RoleMenuEmptyListException(String message){
        super(message);
    }
}

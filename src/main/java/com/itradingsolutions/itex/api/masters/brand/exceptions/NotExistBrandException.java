package com.itradingsolutions.itex.api.masters.brand.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistBrandException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 3253721528022035082L;

    public NotExistBrandException(String message) {
        super(message);
    }
}

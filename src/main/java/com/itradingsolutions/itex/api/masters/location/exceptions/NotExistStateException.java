package com.itradingsolutions.itex.api.masters.location.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotExistStateException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 3482175049331171779L;

    public NotExistStateException(final String message) {
        super(message);
    }
}

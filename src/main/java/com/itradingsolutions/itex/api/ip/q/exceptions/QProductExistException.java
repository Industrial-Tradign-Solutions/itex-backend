package com.itradingsolutions.itex.api.ip.q.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class QProductExistException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = -1L;

    public QProductExistException(String message) {
        super(message);
    }
}

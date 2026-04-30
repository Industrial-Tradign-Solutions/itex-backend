package com.itradingsolutions.itex.api.common.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

public class HistoryException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = 6817692949806114483L;

    public HistoryException(String message) {
        super(message);
    }
}

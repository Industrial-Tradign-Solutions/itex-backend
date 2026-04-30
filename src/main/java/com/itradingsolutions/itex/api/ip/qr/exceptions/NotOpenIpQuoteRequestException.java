package com.itradingsolutions.itex.api.ip.qr.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class NotOpenIpQuoteRequestException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = 1134130148449189529L;

    public NotOpenIpQuoteRequestException() {
    }

    public NotOpenIpQuoteRequestException(String message) {
        super(message);
    }
}

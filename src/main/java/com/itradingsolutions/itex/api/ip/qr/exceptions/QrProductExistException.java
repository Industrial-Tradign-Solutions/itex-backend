package com.itradingsolutions.itex.api.ip.qr.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

import java.io.Serial;

public class QrProductExistException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 6003956624934645016L;

    public QrProductExistException(String message) {
        super(message);
    }
}

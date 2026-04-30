package com.itradingsolutions.itex.api.partners.clients.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

public class ClientErrorFormException extends NotFoundException {
    public ClientErrorFormException(String message) {
        super(message);
    }
}

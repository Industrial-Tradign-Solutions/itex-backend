package com.itradingsolutions.itex.api.partners.suppliers.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;

public class SupplierErrorFormException extends NotFoundException {
    public SupplierErrorFormException(String message) {
        super(message);
    }
}

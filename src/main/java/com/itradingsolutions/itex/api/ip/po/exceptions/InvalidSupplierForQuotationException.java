package com.itradingsolutions.itex.api.ip.po.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

public class InvalidSupplierForQuotationException extends BadRequestException {

    @Serial
    private static final long serialVersionUID = -8691270449373452293L;

    public InvalidSupplierForQuotationException(String message) {
        super(message);
    }
}

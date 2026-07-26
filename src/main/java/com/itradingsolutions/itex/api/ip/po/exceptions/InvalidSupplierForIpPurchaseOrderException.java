package com.itradingsolutions.itex.api.ip.po.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

public class InvalidSupplierForIpPurchaseOrderException extends BadRequestException {

    @Serial
    private static final long serialVersionUID = -8691270449373452293L;

    public InvalidSupplierForIpPurchaseOrderException(String message) {
        super(message);
    }
}

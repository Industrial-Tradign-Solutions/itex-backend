package com.itradingsolutions.itex.api.ip.po.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

public class IpPurchaseOrderNotEditableException extends BadRequestException {

    @Serial
    private static final long serialVersionUID = -6273945108234761584L;

    public IpPurchaseOrderNotEditableException(String message) {
        super(message);
    }
}

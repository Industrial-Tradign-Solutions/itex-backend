package com.itradingsolutions.itex.api.ip.po.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

public class IpPurchaseOrderInvalidQuotationException extends BadRequestException {

    @Serial
    private static final long serialVersionUID = 5601827394857102938L;

    public IpPurchaseOrderInvalidQuotationException(String message) {
        super(message);
    }
}

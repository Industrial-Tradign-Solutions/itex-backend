package com.itradingsolutions.itex.api.ip.po.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

public class IpPurchaseOrderQuotationNotAssignedException extends BadRequestException {

    @Serial
    private static final long serialVersionUID = 8102947561039284756L;

    public IpPurchaseOrderQuotationNotAssignedException(String message) {
        super(message);
    }
}

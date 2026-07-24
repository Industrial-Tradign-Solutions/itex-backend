package com.itradingsolutions.itex.api.ip.po.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

public class IpPurchaseOrderProductNotEligibleException extends BadRequestException {

    @Serial
    private static final long serialVersionUID = -3184756201938475610L;

    public IpPurchaseOrderProductNotEligibleException(String message) {
        super(message);
    }
}

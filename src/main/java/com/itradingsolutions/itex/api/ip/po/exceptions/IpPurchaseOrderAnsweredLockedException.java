package com.itradingsolutions.itex.api.ip.po.exceptions;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;

import java.io.Serial;

public class IpPurchaseOrderAnsweredLockedException extends BadRequestException {

    @Serial
    private static final long serialVersionUID = 4127598320165483927L;

    public IpPurchaseOrderAnsweredLockedException(String message) {
        super(message);
    }
}

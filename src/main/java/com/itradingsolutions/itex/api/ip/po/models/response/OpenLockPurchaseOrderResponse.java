package com.itradingsolutions.itex.api.ip.po.models.response;

public record OpenLockPurchaseOrderResponse(
        PurchaseOrderResponse data,
        boolean isValidOpen
) {}

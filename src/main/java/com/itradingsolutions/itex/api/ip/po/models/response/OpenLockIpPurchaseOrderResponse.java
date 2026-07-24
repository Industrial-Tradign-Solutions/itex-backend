package com.itradingsolutions.itex.api.ip.po.models.response;

public record OpenLockIpPurchaseOrderResponse(
        IpPurchaseOrderResponse data,
        boolean isValidOpen
) {}

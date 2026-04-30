package com.itradingsolutions.itex.api.ip.products.models.responses;

public record OpenLockIpProductResponse(
        IpProductResponse data,
        boolean isValidOpen
) {
}

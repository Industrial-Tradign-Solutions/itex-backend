package com.itradingsolutions.itex.api.ip.qr.models.responses;

public record OpenLockIpQuoteRequestResponse(
        IpQuoteRequestResponse data,
        boolean isValidOpen
) {}

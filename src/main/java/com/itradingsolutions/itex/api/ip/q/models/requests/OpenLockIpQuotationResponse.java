package com.itradingsolutions.itex.api.ip.q.models.requests;

import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationResponse;

public record OpenLockIpQuotationResponse(
        IpQuotationResponse data,
        boolean isValidOpen
) {
}

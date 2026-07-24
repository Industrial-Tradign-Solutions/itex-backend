package com.itradingsolutions.itex.api.ip.po.models.response;

import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationProductResponse;

import java.util.UUID;

public record IpPurchaseOrderProductResponse(
        UUID id,
        IpQuotationProductResponse quotationProduct,
        Integer number
) {
}
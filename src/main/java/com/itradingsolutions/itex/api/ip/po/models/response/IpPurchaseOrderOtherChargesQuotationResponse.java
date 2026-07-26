package com.itradingsolutions.itex.api.ip.po.models.response;

import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationOtherChargeResponse;

import java.util.UUID;

public record IpPurchaseOrderOtherChargesQuotationResponse(
        UUID id,
        IpQuotationOtherChargeResponse quotationOtherCharge
) {
}
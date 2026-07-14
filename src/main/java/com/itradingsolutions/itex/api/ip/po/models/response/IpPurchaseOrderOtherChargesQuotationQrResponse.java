package com.itradingsolutions.itex.api.ip.po.models.response;

import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationOtherChargesQuoteRequestResponse;

import java.util.UUID;

public record IpPurchaseOrderOtherChargesQuotationQrResponse(
        UUID id,
        IpQuotationOtherChargesQuoteRequestResponse quotationQrOtherCharge
) {
}
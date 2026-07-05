package com.itradingsolutions.itex.api.ip.q.models.response;

import com.itradingsolutions.itex.api.ip.qr.models.responses.IpQuoteRequestOtherChargeResponse;

import java.util.UUID;

public record IpQuotationOtherChargesQuoteRequestResponse(
        UUID id,
        UUID quotationsQuoteRequestId,
        IpQuoteRequestOtherChargeResponse qrOtherCharge
) {
}

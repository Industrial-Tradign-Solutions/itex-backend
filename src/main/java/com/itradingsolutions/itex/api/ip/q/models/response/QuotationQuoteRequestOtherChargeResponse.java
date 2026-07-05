package com.itradingsolutions.itex.api.ip.q.models.response;

import java.math.BigDecimal;

public record QuotationQuoteRequestOtherChargeResponse(
        String description,
        BigDecimal value,
        String qrNumber
) {
}
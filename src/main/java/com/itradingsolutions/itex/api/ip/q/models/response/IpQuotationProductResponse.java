package com.itradingsolutions.itex.api.ip.q.models.response;

import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationProductCondition;
import com.itradingsolutions.itex.api.ip.qr.models.responses.IpQuoteRequestProductResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record IpQuotationProductResponse(
        UUID id,
        UUID quotationsQuoteRequestId,
        IpQuoteRequestProductResponse quoteRequestProduct,
        Integer number,
        BigDecimal profitMargin,
        IpQuotationProductCondition condition,
        BigDecimal sellingUnitPrice,
        BigDecimal extendedPrice,
        BigDecimal grossWeightLbs
) {}

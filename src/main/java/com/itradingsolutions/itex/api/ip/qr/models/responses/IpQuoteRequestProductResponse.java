package com.itradingsolutions.itex.api.ip.qr.models.responses;

import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.models.enums.UnitType;
import com.itradingsolutions.itex.api.ip.products.models.responses.IpProductResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record IpQuoteRequestProductResponse(
        UUID id,
        IpProductResponse ipProduct,
        Integer number,
        BigDecimal quantity,
        UnitType unitType,
        Integer leadTime,
        LeadTime leadTimeType,
        BigDecimal unitPrice,
        BigDecimal extendedPrice
) {}

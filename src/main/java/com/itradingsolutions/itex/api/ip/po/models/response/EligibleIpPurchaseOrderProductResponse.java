package com.itradingsolutions.itex.api.ip.po.models.response;

import java.math.BigDecimal;
import java.util.UUID;

public record EligibleIpPurchaseOrderProductResponse(
        UUID quotationProductId,
        String description,
        String mfrReference,
        BigDecimal quantity,
        BigDecimal sellingUnitPrice,
        BigDecimal sellingExtendedPrice,
        String qrNumber
) {
}

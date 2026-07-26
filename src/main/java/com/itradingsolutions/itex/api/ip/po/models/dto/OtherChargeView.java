package com.itradingsolutions.itex.api.ip.po.models.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OtherChargeView(
        UUID id,
        BigDecimal value,
        String description,
        String source
) {
    public static final String OWN = "OWN";
    public static final String QUOTATION = "QUOTATION";
    public static final String QUOTATION_QR = "QUOTATION_QR";
}

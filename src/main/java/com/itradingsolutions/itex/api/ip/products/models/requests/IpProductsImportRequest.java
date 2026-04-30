package com.itradingsolutions.itex.api.ip.products.models.requests;

import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;

public record IpProductsImportRequest(
        String brandStr,
        @NotEmpty(message = "Description is required")
        String description,
        String clientDescription,
        @NotEmpty(message = "MFR Reference is required")
        String mfrReference,
        String clientReference,
        BigDecimal netWeightLbs,
        Integer nmfc,
        FreightClass freightClass,
        Integer htsScheduleBNumber,
        String eccn,
        String cooStr,
        boolean battery,
        boolean hazmat,
        boolean dualUse
) {
}

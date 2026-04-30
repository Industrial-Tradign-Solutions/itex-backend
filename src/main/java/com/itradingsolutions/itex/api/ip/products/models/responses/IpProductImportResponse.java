package com.itradingsolutions.itex.api.ip.products.models.responses;

import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import com.itradingsolutions.itex.api.masters.brand.models.responses.BasicBrandResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.BasicCountryResponse;

import java.math.BigDecimal;
import java.util.List;

public record IpProductImportResponse(
        BasicBrandResponse brand,
        String description,
        String clientDescription,
        String mfrReference,
        String clientReference,
        BigDecimal netWeightLbs,
        Integer nmfc,
        FreightClass freightClass,
        Integer htsScheduleBNumber,
        String eccn,
        BasicCountryResponse coo,
        boolean battery,
        boolean hazmat,
        boolean dualUse,
        List<String> importErrors,
        boolean validImport,
        boolean saveBrand,
        boolean saveCoo
) {
}

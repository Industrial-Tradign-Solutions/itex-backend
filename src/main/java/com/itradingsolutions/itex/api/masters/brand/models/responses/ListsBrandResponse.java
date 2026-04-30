package com.itradingsolutions.itex.api.masters.brand.models.responses;

import java.util.List;

public record ListsBrandResponse(
        List<BasicBrandResponse> enableBrands,
        List<BasicBrandResponse> disableBrands
) {

}

package com.itradingsolutions.itex.api.ip.po.models.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record AddIpPurchaseOrderProductsRequest(
        @NotEmpty(message = "At least one quotationProductId is required") List<UUID> quotationProductIds
) {
}

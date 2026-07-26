package com.itradingsolutions.itex.api.ip.po.models.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ImportIpPurchaseOrderOtherChargesRequest(
        @NotEmpty(message = "At least one charge id is required") List<UUID> chargeIds
) {
}

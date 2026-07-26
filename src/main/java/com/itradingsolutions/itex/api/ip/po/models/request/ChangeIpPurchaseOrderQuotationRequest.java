package com.itradingsolutions.itex.api.ip.po.models.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeIpPurchaseOrderQuotationRequest(
        @NotNull(message = "Quotation is required") UUID quotationId
) {
}

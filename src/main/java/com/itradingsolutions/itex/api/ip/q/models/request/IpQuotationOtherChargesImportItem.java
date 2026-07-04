package com.itradingsolutions.itex.api.ip.q.models.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record IpQuotationOtherChargesImportItem(
        @NotNull UUID quotationsQuoteRequestId,
        @NotNull UUID qrOtherChargeId
) {
}

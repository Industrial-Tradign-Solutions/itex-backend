package com.itradingsolutions.itex.api.ip.po.models.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePurchaseOrderRequest(
        @NotNull(message = "Client is required")
        UUID clientId,

        UUID quotationId,

        UUID supplierId
) {}

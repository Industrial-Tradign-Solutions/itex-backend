package com.itradingsolutions.itex.api.ip.q.models.requests;

import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateIpQuotationRequest(
        @NotNull(message = "Client is required")
        UUID clientId,
        @NotNull(message = "Currency is required")
        Currency currency,
        List<UUID> listQrId,
        @NotNull(message = "Application date is required")
        @JsonFormat(pattern = "MM-dd-yyyy")
        LocalDate applicationAt
) {
}

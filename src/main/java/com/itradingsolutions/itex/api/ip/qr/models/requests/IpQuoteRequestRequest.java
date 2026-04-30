package com.itradingsolutions.itex.api.ip.qr.models.requests;

import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record IpQuoteRequestRequest(
        @NotNull(message = "Currency is required")
        Currency currency,

        @NotNull(message = "Client is required")
        UUID clientId,

        UUID clientContactId,

        String clientQrNumber,

        UUID salesRepId,

        @NotNull(message = "Supplier is required")
        UUID supplierId,

        UUID supplierContactId,

        String supplierQrNumber,

        String remarks,

        String internalRemarks,

        String shippingPointZipCode,

        FreightClass freightClass,

        String fobShippingPoint,

        PaymentTerms paymentTerms,

        BigDecimal freightCharges
) {}

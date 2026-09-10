package com.itradingsolutions.itex.api.ip.q.models.requests;

import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.Incoterms;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateIpQuotationRequest(

        @NotNull(message = "Client is required")
        UUID clientId,

        @NotNull(message = "Currency is required")
        Currency currency,

        UUID clientContactId,

        String clientQrNumber,

        UUID salesRepId,

        String remarks,

        String internalRemarks,

        @Min(value = 0, message = "The lead time cannot be negative")
        Integer leadTime,

        LeadTime leadTimeType,

        @Min(value = 0, message = "The validity cannot be negative")
        Integer validity,

        LeadTime validityType,

        Incoterms incoterms,

        PaymentTerms paymentTerms,

        @Min(value = 0, message = "The freight profit margin cannot be negative")
        BigDecimal profitMarginFreightCharges,

        @Min(value = 0, message = "The Miami freight charge cannot be negative")
        BigDecimal freightChargeMiamiITS,

        @NotNull(message = "Application date is required")
        @JsonFormat(pattern = "MM-dd-yyyy")
        LocalDate applicationAt
) {
}

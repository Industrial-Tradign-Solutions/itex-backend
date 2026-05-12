package com.itradingsolutions.itex.api.ip.q.models.requests;

import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.Incoterms;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateIpQuotationRequest(
        @NotNull(message = "{ip.q.client.required}") UUID clientId,
        @NotNull(message = "{ip.q.currency.required}") Currency currency,
        UUID clientContactId,
        @Size(max = 50, message = "{ip.q.client-qr-number.size}") String clientQrNumber,
        @NotNull(message = "{ip.q.sales-rep.required}") UUID salesRepId,
        String remarks,
        String internalRemarks,
        @Positive(message = "{ip.q.lead-time.invalid}") Integer leadTime,
        LeadTime leadTimeType,
        @Positive(message = "{ip.q.validity.invalid}") Integer validity,
        LeadTime validityType,
        Incoterms incoterms,
        PaymentTerms paymentTerms
) {
}
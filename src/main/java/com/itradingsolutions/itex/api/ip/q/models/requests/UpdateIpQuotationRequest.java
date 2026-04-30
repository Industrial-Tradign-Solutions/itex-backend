package com.itradingsolutions.itex.api.ip.q.models.requests;

import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.models.enums.Incoterms;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;

import java.util.UUID;

public record UpdateIpQuotationRequest(
        UUID clientContactId,
        String clientQrNumber,
        UUID salesRepId,
        String remarks,
        String internalRemarks,
        Integer leadTime,
        LeadTime leadTimeType,
        Integer validity,
        LeadTime validityType,
        Incoterms incoterms,
        PaymentTerms paymentTerms
) {
}

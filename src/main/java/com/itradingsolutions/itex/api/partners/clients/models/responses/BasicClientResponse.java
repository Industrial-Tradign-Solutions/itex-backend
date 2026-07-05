package com.itradingsolutions.itex.api.partners.clients.models.responses;

import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;

import java.util.List;
import java.util.UUID;

public record BasicClientResponse(
        UUID id,
        String code,
        String name,
        String address,
        String showName,
        PaymentTerms paymentTerms,
        List<ClientInfoDepResponse> infoByDepartment
) {
}

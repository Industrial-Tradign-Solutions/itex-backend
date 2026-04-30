package com.itradingsolutions.itex.api.partners.suppliers.models.responses;

import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;

import java.util.List;
import java.util.UUID;

public record BasicSupplierResponse(
        UUID id,
        String name,
        String address,
        PaymentTerms paymentTerms,
        List<SupplierInfoDepResponse> infoByDepartment
) {
}

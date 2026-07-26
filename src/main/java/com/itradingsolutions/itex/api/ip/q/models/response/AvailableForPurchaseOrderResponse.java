package com.itradingsolutions.itex.api.ip.q.models.response;

import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.BasicSupplierResponse;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record AvailableForPurchaseOrderResponse(
        UUID id,
        String number,
        String name,
        IpQuotationStatus status,
        ZonedDateTime applicationAt,
        List<BasicSupplierResponse> suppliers
) {
}

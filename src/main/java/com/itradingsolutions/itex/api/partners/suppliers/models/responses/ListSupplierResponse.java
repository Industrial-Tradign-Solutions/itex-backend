package com.itradingsolutions.itex.api.partners.suppliers.models.responses;

import com.itradingsolutions.itex.api.partners.suppliers.models.enums.SupplierStatus;

import java.util.UUID;

public record ListSupplierResponse(
        UUID id,
        String name,
        String taxId,
        String city,
        String address,
        SupplierStatus status
) {
}

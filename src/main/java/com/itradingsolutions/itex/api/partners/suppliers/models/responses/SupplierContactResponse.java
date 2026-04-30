package com.itradingsolutions.itex.api.partners.suppliers.models.responses;

import java.util.List;
import java.util.UUID;

public record SupplierContactResponse(
        UUID id,
        String name,
        String title,
        String email,
        boolean validMain,
        boolean active,
        List<SupplierContactPhoneResponse>listPhones,
        String mainPhone
) {
}

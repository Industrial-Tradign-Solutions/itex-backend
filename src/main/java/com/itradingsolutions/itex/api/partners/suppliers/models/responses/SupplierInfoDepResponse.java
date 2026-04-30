package com.itradingsolutions.itex.api.partners.suppliers.models.responses;

import com.itradingsolutions.itex.api.masters.department.models.responses.BasicDepartmentResponse;

import java.util.List;
import java.util.UUID;

public record SupplierInfoDepResponse(
        UUID id,
        BasicDepartmentResponse department,
        List<SupplierContactResponse>listContacts,
        String notes
) {
}

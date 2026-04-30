package com.itradingsolutions.itex.api.partners.clients.models.responses;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.masters.department.models.responses.BasicDepartmentResponse;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ClientInfoDepResponse(
        UUID id,
        BasicUserResponse accountRep,
        BasicDepartmentResponse department,
        BigDecimal target,
        List<ClientContactResponse>listContacts,
        String notes
) {
}

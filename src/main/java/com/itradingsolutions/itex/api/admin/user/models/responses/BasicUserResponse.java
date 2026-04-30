package com.itradingsolutions.itex.api.admin.user.models.responses;

import com.itradingsolutions.itex.api.admin.role.models.responses.BasicRoleResponse;
import com.itradingsolutions.itex.api.masters.department.models.responses.BasicDepartmentResponse;


import java.util.List;
import java.util.UUID;

public record BasicUserResponse(
        UUID id,
        String fullName,
        List<BasicDepartmentResponse> departments,
        BasicRoleResponse role,
        boolean active
) {
}

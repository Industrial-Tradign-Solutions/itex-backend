package com.itradingsolutions.itex.api.admin.user.models.responses;

import com.itradingsolutions.itex.api.admin.role.models.responses.BasicRoleResponse;
import com.itradingsolutions.itex.api.masters.department.models.responses.BasicDepartmentResponse;


import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record ListUserResponse (
        UUID id,
        String name,
        String lastName,
        String email,
        String user,
        boolean active,
        BasicRoleResponse role,
        List<BasicDepartmentResponse> departments,
        boolean passChanged,
        ZonedDateTime createdAt,
        String title,
        String extension
) {
}

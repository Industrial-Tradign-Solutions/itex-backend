package com.itradingsolutions.itex.api.admin.user.models.responses;

import com.itradingsolutions.itex.api.admin.role.models.responses.BasicRoleResponse;
import com.itradingsolutions.itex.api.masters.department.models.responses.BasicDepartmentResponse;


import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String lastName,
        String email,
        String user,
        Boolean active,
        BasicRoleResponse role,
        List<BasicDepartmentResponse> departments,
        String emailPassword,
        Boolean passChanged,
        ZonedDateTime passChangedAt,
        ZonedDateTime createdAt,
        String title,
        String extension
) {
}

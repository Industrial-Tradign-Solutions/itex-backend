package com.itradingsolutions.itex.api.partners.clients.models.responses;

import java.util.List;
import java.util.UUID;

public record BasicClientResponse(
        UUID id,
        String code,
        String name,
        String address,
        String showName,
        List<ClientInfoDepResponse> infoByDepartment
) {
}

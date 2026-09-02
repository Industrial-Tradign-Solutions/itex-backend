package com.itradingsolutions.itex.api.partners.clients.models.dto;

import java.util.List;

public record ClientMissingInfo(
        List<String> errors,
        ClientDTO client
) {
}

package com.itradingsolutions.itex.api.ip.q.models.response;

import java.util.UUID;

public record BasicQuotationResponse(
        UUID id,
        String number
) {
}

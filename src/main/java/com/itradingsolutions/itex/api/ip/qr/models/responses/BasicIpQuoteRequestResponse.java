package com.itradingsolutions.itex.api.ip.qr.models.responses;

import java.util.UUID;

public record BasicIpQuoteRequestResponse(
        UUID qqrId,
        UUID id,
        String number
) {
}

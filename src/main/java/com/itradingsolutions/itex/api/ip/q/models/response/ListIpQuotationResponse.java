package com.itradingsolutions.itex.api.ip.q.models.response;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientResponse;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ListIpQuotationResponse(
        UUID id,
        String number,
        String name,
        ClientResponse client,
        IpQuotationStatus status,
        BasicUserResponse salesRep,
        ZonedDateTime applicationAt,
        ZonedDateTime createdAt
) {
}

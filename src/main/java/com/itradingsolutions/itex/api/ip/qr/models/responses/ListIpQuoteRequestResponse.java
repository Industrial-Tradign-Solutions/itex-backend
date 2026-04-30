package com.itradingsolutions.itex.api.ip.qr.models.responses;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientResponse;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.SupplierResponse;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ListIpQuoteRequestResponse(
        UUID id,
        String number,
        String name,
        ClientResponse client,
        SupplierResponse supplier,
        IpQuoteRequestStatus status,
        BasicUserResponse salesRep,
        ZonedDateTime createdAt
) {
}

package com.itradingsolutions.itex.api.ip.po.models.response;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderStatus;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientResponse;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.SupplierResponse;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ListIpPurchaseOrderResponse(
        UUID id,
        String number,
        String name,
        ClientResponse client,
        SupplierResponse supplier,
        IpPurchaseOrderStatus status,
        BasicUserResponse salesRep,
        ZonedDateTime createdAt
) {
}

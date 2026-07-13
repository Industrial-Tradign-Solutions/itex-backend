package com.itradingsolutions.itex.api.ip.po.models.response;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.ip.po.models.enums.PurchaseOrderStatus;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientResponse;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.SupplierResponse;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ListPurchaseOrderResponse(
        UUID id,
        String number,
        String name,
        ClientResponse client,
        SupplierResponse supplier,
        PurchaseOrderStatus status,
        BasicUserResponse salesRep,
        ZonedDateTime createdAt
) {
}

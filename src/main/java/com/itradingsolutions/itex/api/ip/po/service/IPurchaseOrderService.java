package com.itradingsolutions.itex.api.ip.po.service;

import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderDTO;
import com.itradingsolutions.itex.api.ip.po.models.request.CreatePurchaseOrderRequest;

import java.util.List;
import java.util.UUID;

public interface IPurchaseOrderService {

    PurchaseOrderDTO createPurchaseOrder(CreatePurchaseOrderRequest request);

    PurchaseOrderDTO findById(UUID id);

    PurchaseOrderDTO clonePurchaseOrder(UUID id);

    PurchaseOrderDTO openAndLockPurchaseOrder(UUID id, OpenAndLockType type);

    void unlockPurchaseOrder(UUID id);

    int batchUnlock(List<UUID> ids);

    List<PurchaseOrderDTO> listAllOpenByUser(String username);
}

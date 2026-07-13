package com.itradingsolutions.itex.api.ip.po.service;

import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderDTO;
import com.itradingsolutions.itex.api.ip.po.models.request.CreatePurchaseOrderRequest;

public interface IPurchaseOrderService {

    PurchaseOrderDTO createPurchaseOrder(CreatePurchaseOrderRequest request);
}

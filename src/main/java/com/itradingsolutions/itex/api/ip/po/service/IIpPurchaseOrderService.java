package com.itradingsolutions.itex.api.ip.po.service;

import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderDTO;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderStatus;
import com.itradingsolutions.itex.api.ip.po.models.filters.FilterListIpPurchaseOrder;
import com.itradingsolutions.itex.api.ip.po.models.request.CreateIpPurchaseOrderRequest;
import com.itradingsolutions.itex.api.ip.po.models.request.UpdateIpPurchaseOrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IIpPurchaseOrderService {

    IpPurchaseOrderDTO createIpPurchaseOrder(CreateIpPurchaseOrderRequest request);

    IpPurchaseOrderDTO updateIpPurchaseOrder(UUID id, UpdateIpPurchaseOrderRequest request);

    IpPurchaseOrderDTO removeQuotationFromPurchaseOrder(UUID id);

    IpPurchaseOrderDTO changeQuotationOfPurchaseOrder(UUID id, UUID newQuotationId);

    IpPurchaseOrderDTO changeStatusIpPurchaseOrder(UUID id, IpPurchaseOrderStatus newStatus);

    IpPurchaseOrderDTO rejectIpPurchaseOrder(UUID id);

    void unlockAllOpenIpPurchaseOrders();

    void autoRejectOldCreatedIpPurchaseOrders();

    IpPurchaseOrderDTO findById(UUID id);

    IpPurchaseOrderDTO cloneIpPurchaseOrder(UUID id);

    IpPurchaseOrderDTO openAndLockIpPurchaseOrder(UUID id, OpenAndLockType type);

    void unlockIpPurchaseOrder(UUID id);

    int batchUnlock(List<UUID> ids);

    Page<IpPurchaseOrderDTO> listAll(Pageable pageable, FilterListIpPurchaseOrder filters);

    List<IpPurchaseOrderDTO> listAllOpenByUser(String username);

    byte[] printPurchaseOrder(UUID id);
}

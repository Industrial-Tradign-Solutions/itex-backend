package com.itradingsolutions.itex.api.ip.po.service;

import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderProductDTO;
import com.itradingsolutions.itex.api.ip.po.models.response.EligibleIpPurchaseOrderProductResponse;

import java.util.List;
import java.util.UUID;

public interface IIpPurchaseOrderProductService {

    List<EligibleIpPurchaseOrderProductResponse> getEligibleProducts(UUID poId);

    List<IpPurchaseOrderProductDTO> addProducts(UUID poId, List<UUID> quotationProductIds);

    IpPurchaseOrderProductDTO get(UUID id, UUID poId);

    void remove(UUID id, UUID poId);
}

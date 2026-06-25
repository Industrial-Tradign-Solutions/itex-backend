package com.itradingsolutions.itex.api.ip.q.service;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;

import java.util.List;
import java.util.UUID;

public interface IIpQuotationProductService {
    List<IpQuotationProductDTO> createIpQuotationProducts(List<IpQuotationProductDTO> productRequests, UUID quotationId);
    IpQuotationProductDTO updateIpQuotationProduct(IpQuotationProductDTO productRequest, UUID qProductId, UUID quotationId);
    IpQuotationProductDTO getIpQuotationProduct(UUID qProductId, UUID quotationId);
    void removeIpQuotationProduct(UUID qProductId, UUID quotationId);
}

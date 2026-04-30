package com.itradingsolutions.itex.api.ip.q.service;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;

import java.util.UUID;

public interface IIpQuotationProductService {
    IpQuotationProductDTO createIpQuotationProduct(IpQuotationProductDTO productRequest, UUID quotationId);
    IpQuotationProductDTO updateIpQuotationProduct(IpQuotationProductDTO productRequest, UUID qProductId, UUID quotationId);
    IpQuotationProductDTO getIpQuotationProduct(UUID qProductId, UUID quotationId);
    void removeIpQuotationProduct(UUID qProductId, UUID quotationId);
}

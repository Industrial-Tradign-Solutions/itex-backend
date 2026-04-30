package com.itradingsolutions.itex.api.ip.qr.service;

import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestProductDTO;

import java.util.UUID;

public interface IIpQuoteRequestProductService {
    IpQuoteRequestProductDTO createIpQuoteRequestProduct(IpQuoteRequestProductDTO productRequest, UUID qrId);
    IpQuoteRequestProductDTO updateIpQuoteRequestProduct(IpQuoteRequestProductDTO productRequest, UUID qrProductId, UUID qrId);
    IpQuoteRequestProductDTO getIpQuoteRequestProduct(UUID qrProductId, UUID qrId);
    void removeIpQuoteRequestProduct(UUID qrProductId, UUID qrId);
}

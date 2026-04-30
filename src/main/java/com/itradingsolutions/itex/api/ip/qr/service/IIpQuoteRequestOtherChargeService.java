package com.itradingsolutions.itex.api.ip.qr.service;

import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestOtherChargesDTO;

import java.util.UUID;

public interface IIpQuoteRequestOtherChargeService {

    IpQuoteRequestOtherChargesDTO create(IpQuoteRequestOtherChargesDTO request, UUID qrId);
    IpQuoteRequestOtherChargesDTO update(IpQuoteRequestOtherChargesDTO request, UUID qrOtherChargeId, UUID qrId);
    IpQuoteRequestOtherChargesDTO get(UUID qrOtherChargeId, UUID qrId);
    void remove(UUID qrOtherChargeId, UUID qrId);
}

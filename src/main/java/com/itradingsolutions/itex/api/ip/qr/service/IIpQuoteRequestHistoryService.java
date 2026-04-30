package com.itradingsolutions.itex.api.ip.qr.service;

import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestHistoryDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestOtherChargesDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestProductDTO;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestHistoryAction;

import java.util.List;
import java.util.UUID;

public interface IIpQuoteRequestHistoryService {

    void addHistory(IpQuoteRequestHistoryAction action, IpQuoteRequestDTO oldDto, IpQuoteRequestDTO newDto);
    void addHistoryProduct(IpQuoteRequestHistoryAction action, IpQuoteRequestProductDTO oldDto, IpQuoteRequestProductDTO newDto, UUID qrId);
    void addHistoryOtherCharge(IpQuoteRequestHistoryAction action, IpQuoteRequestOtherChargesDTO oldDto, IpQuoteRequestOtherChargesDTO newDto, UUID qrId);
    List<IpQuoteRequestHistoryDTO> getHistoryById(UUID id);
}

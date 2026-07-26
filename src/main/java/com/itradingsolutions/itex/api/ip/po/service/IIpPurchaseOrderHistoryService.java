package com.itradingsolutions.itex.api.ip.po.service;

import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderHistoryDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargesQuotationDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargesQuotationQrDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderProductDTO;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderHistoryAction;

import java.util.List;
import java.util.UUID;

public interface IIpPurchaseOrderHistoryService {

    void addHistory(IpPurchaseOrderHistoryAction action, IpPurchaseOrderDTO oldDto, IpPurchaseOrderDTO newDto);
    void addHistoryProduct(IpPurchaseOrderHistoryAction action, IpPurchaseOrderProductDTO oldDto, IpPurchaseOrderProductDTO newDto, UUID poId);
    void addHistoryOtherCharge(IpPurchaseOrderHistoryAction action, IpPurchaseOrderOtherChargeDTO oldDto, IpPurchaseOrderOtherChargeDTO newDto, UUID poId);
    void addHistoryImportedQCharge(IpPurchaseOrderHistoryAction action, IpPurchaseOrderOtherChargesQuotationDTO oldDto, IpPurchaseOrderOtherChargesQuotationDTO newDto, UUID poId);
    void addHistoryImportedQrCharge(IpPurchaseOrderHistoryAction action, IpPurchaseOrderOtherChargesQuotationQrDTO oldDto, IpPurchaseOrderOtherChargesQuotationQrDTO newDto, UUID poId);
    List<IpPurchaseOrderHistoryDTO> getHistoryById(UUID id);
}

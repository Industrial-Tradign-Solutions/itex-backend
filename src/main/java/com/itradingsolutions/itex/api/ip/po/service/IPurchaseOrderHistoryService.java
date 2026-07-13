package com.itradingsolutions.itex.api.ip.po.service;

import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderHistoryDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderOtherChargesQuotationDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderOtherChargesQuotationQrDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderProductDTO;
import com.itradingsolutions.itex.api.ip.po.models.enums.PurchaseOrderHistoryAction;

import java.util.List;
import java.util.UUID;

public interface IPurchaseOrderHistoryService {

    void addHistory(PurchaseOrderHistoryAction action, PurchaseOrderDTO oldDto, PurchaseOrderDTO newDto);
    void addHistoryProduct(PurchaseOrderHistoryAction action, PurchaseOrderProductDTO oldDto, PurchaseOrderProductDTO newDto, UUID poId);
    void addHistoryOtherCharge(PurchaseOrderHistoryAction action, PurchaseOrderOtherChargeDTO oldDto, PurchaseOrderOtherChargeDTO newDto, UUID poId);
    void addHistoryImportedQCharge(PurchaseOrderHistoryAction action, PurchaseOrderOtherChargesQuotationDTO oldDto, PurchaseOrderOtherChargesQuotationDTO newDto, UUID poId);
    void addHistoryImportedQrCharge(PurchaseOrderHistoryAction action, PurchaseOrderOtherChargesQuotationQrDTO oldDto, PurchaseOrderOtherChargesQuotationQrDTO newDto, UUID poId);
    List<PurchaseOrderHistoryDTO> getHistoryById(UUID id);
}

package com.itradingsolutions.itex.api.ip.po.service;

import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargesQuotationDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargesQuotationQrDTO;
import com.itradingsolutions.itex.api.ip.po.models.response.AvailableIpPurchaseOrderOtherChargeResponse;

import java.util.List;
import java.util.UUID;

public interface IIpPurchaseOrderOtherChargeService {

    IpPurchaseOrderOtherChargeDTO create(IpPurchaseOrderOtherChargeDTO request, UUID poId);

    IpPurchaseOrderOtherChargeDTO update(IpPurchaseOrderOtherChargeDTO request, UUID otherChargeId, UUID poId);

    IpPurchaseOrderOtherChargeDTO get(UUID otherChargeId, UUID poId);

    void remove(UUID otherChargeId, UUID poId);

    List<IpPurchaseOrderOtherChargesQuotationDTO> importFromQuotation(List<UUID> quotationOtherChargeIds, UUID poId);

    List<AvailableIpPurchaseOrderOtherChargeResponse> getAvailableFromQuotation(UUID poId);

    IpPurchaseOrderOtherChargesQuotationDTO getImportedFromQuotation(UUID id, UUID poId);

    void removeImportedFromQuotation(UUID id, UUID poId);

    List<IpPurchaseOrderOtherChargesQuotationQrDTO> importFromQuotationQr(List<UUID> quotationQrOtherChargeIds, UUID poId);

    List<AvailableIpPurchaseOrderOtherChargeResponse> getAvailableFromQuotationQr(UUID poId);

    IpPurchaseOrderOtherChargesQuotationQrDTO getImportedFromQuotationQr(UUID id, UUID poId);

    void removeImportedFromQuotationQr(UUID id, UUID poId);
}

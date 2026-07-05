package com.itradingsolutions.itex.api.ip.q.service;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationOtherChargesQuoteRequestDTO;
import com.itradingsolutions.itex.api.ip.q.models.request.IpQuotationOtherChargesImportItem;
import com.itradingsolutions.itex.api.ip.q.models.response.QuotationAvailableQrOtherChargeResponse;

import java.util.List;
import java.util.UUID;

public interface IIpQuotationOtherChargesQuoteRequestService {

    List<IpQuotationOtherChargesQuoteRequestDTO> bulkImport(List<IpQuotationOtherChargesImportItem> items, UUID quotationId);

    List<IpQuotationOtherChargesQuoteRequestDTO> getImportedByQuotationId(UUID quotationId);

    IpQuotationOtherChargesQuoteRequestDTO get(UUID id, UUID quotationId);

    void remove(UUID id, UUID quotationId);

    List<QuotationAvailableQrOtherChargeResponse> getAvailableQrOtherCharges(UUID quotationId);
}

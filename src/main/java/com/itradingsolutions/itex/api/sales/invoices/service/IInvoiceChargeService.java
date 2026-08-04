package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceChargeDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.request.ImportInvoiceChargesRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.request.InvoiceChargeRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.response.AvailablePoChargeResponse;

import java.util.List;
import java.util.UUID;

public interface IInvoiceChargeService {

    InvoiceChargeDTO create(UUID invoiceId, InvoiceChargeRequest request);

    InvoiceChargeDTO update(UUID invoiceId, UUID chargeId, InvoiceChargeRequest request);

    void remove(UUID invoiceId, UUID chargeId);

    InvoiceChargeDTO get(UUID invoiceId, UUID chargeId);

    List<InvoiceChargeDTO> importFromPo(UUID invoiceId, ImportInvoiceChargesRequest request);

    List<AvailablePoChargeResponse> listAvailableFromPos(UUID invoiceId);
}

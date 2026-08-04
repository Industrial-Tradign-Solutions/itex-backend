package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoicePurchaseOrderDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.request.LinkInvoicePurchaseOrdersRequest;

import java.util.List;
import java.util.UUID;

public interface IInvoiceLinkedPoService {

    List<InvoicePurchaseOrderDTO> link(UUID invoiceId, LinkInvoicePurchaseOrdersRequest request);

    void unlink(UUID invoiceId, UUID poId);
}

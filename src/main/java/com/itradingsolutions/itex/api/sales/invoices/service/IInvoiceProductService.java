package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceProductDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.request.ImportInvoiceProductsRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.request.InvoiceProductRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.response.AvailablePoProductResponse;

import java.util.List;
import java.util.UUID;

public interface IInvoiceProductService {

    InvoiceProductDTO add(UUID invoiceId, InvoiceProductRequest request);

    InvoiceProductDTO update(UUID invoiceId, UUID productId, InvoiceProductRequest request);

    void remove(UUID invoiceId, UUID productId);

    InvoiceProductDTO get(UUID invoiceId, UUID productId);

    List<InvoiceProductDTO> importFromPo(UUID invoiceId, ImportInvoiceProductsRequest request);

    List<AvailablePoProductResponse> listAvailableFromPos(UUID invoiceId);
}

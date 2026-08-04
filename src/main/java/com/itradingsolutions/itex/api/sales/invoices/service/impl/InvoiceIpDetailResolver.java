package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceProductDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoicePurchaseOrderDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceLinkedPoMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceProductMapper;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceIpPoRepository;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceIpProductRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceDepartmentDetailResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvoiceIpDetailResolver implements InvoiceDepartmentDetailResolver {

    private final IInvoiceIpProductRepository productRepository;
    private final IInvoiceIpPoRepository linkedPoRepository;
    private final InvoiceProductMapper productMapper;
    private final InvoiceLinkedPoMapper linkedPoMapper;

    @Override
    public ConsecutiveDepartment department() {
        return ConsecutiveDepartment.IP;
    }

    @Override
    public List<InvoiceProductDTO> products(UUID invoiceId) {
        return productRepository.fetchByInvoiceId(invoiceId).stream()
                .map(productMapper::entityToDTO)
                .toList();
    }

    @Override
    public List<InvoicePurchaseOrderDTO> linkedPurchaseOrders(UUID invoiceId) {
        return linkedPoRepository.fetchByInvoiceId(invoiceId).stream()
                .map(linkedPoMapper::entityToDTO)
                .toList();
    }
}

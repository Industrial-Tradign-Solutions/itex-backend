package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceProductDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoicePurchaseOrderDTO;

import java.util.List;
import java.util.UUID;

/**
 * Where an Invoice's line items and linked source documents come from is department-specific: IP
 * reads {@code t_invoice_ip_products} / {@code t_invoice_ip_po}, and RM/LO/IF will each bring
 * their own tables. One implementation per department, registered as a {@code @Component} so
 * {@link InvoiceDetailResolver} can pick the right one — adding a department is a new class, not a
 * change to this interface or to the resolver.
 */
public interface InvoiceDepartmentDetailResolver {

    ConsecutiveDepartment department();

    List<InvoiceProductDTO> products(UUID invoiceId);

    default List<InvoicePurchaseOrderDTO> linkedPurchaseOrders(UUID invoiceId) {
        return List.of();
    }
}

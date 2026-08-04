package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.NotExistInvoiceException;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * Shared entry point for every Invoice child mutation (products, charges, taxes, PO links). Loads
 * the invoice and enforces the three preconditions each mutation shares: the caller may access the
 * invoice (sales-rep scoping), it is editable (DRAFT), and the caller holds the lock. Keeping this
 * in one place means a new sub-resource cannot forget one of the checks.
 */
@Component
@RequiredArgsConstructor
public class InvoiceChildMutationSupport extends UtilServiceAbs {

    private final IInvoiceRepository repository;
    private final InvoiceAccessGuard accessGuard;
    private final InvoiceMutationGuard mutationGuard;

    /**
     * Loads the invoice and runs the full guard block. The returned entity is managed, so mutating
     * its child collections and saving it cascades the change.
     */
    public InvoiceEntity loadEditable(UUID invoiceId) {
        var invoice = findById(invoiceId);
        accessGuard.assertCanAccess(invoice);
        accessGuard.assertCanMutate(invoice);
        mutationGuard.assertEditable(invoice);
        mutationGuard.assertLockedByCurrentUser(invoice);
        return invoice;
    }

    /**
     * Read-only load with access scoping only (no editable/lock check), for the child GET endpoints.
     */
    public InvoiceEntity loadReadable(UUID invoiceId) {
        var invoice = findById(invoiceId);
        accessGuard.assertCanAccess(invoice);
        return invoice;
    }

    /**
     * Import endpoints copy line items from a PO; that PO must first be linked to the invoice
     * ({@code t_invoice_ip_po}), which is the traceability record the guide requires (§3).
     */
    public void assertPoLinked(InvoiceEntity invoice, UUID poId) {
        var linked = Optional.ofNullable(invoice.getLinkedPurchaseOrders())
                .orElseGet(Collections::emptyList).stream()
                .anyMatch(l -> l.getPurchaseOrder().getId().equals(poId));
        if (!linked)
            throw new BadRequestException(simpleMessage("sales.invoice.po.not-linked"));
    }

    private InvoiceEntity findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotExistInvoiceException(simpleMessage("sales.invoice.not-exist")));
    }
}

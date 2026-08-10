package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
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
    private final InvoiceFinder finder;
    private final InvoiceAccessGuard accessGuard;
    private final InvoiceMutationGuard mutationGuard;
    private final InvoiceAmountCalculator calculator;

    /**
     * Loads the invoice and runs the full guard block. The returned entity is managed, so mutating
     * its child collections and saving it cascades the change.
     */
    public InvoiceEntity loadEditable(UUID invoiceId) {
        var invoice = finder.findById(invoiceId);
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
        var invoice = finder.findById(invoiceId);
        accessGuard.assertCanAccess(invoice);
        return invoice;
    }

    /**
     * The closing pair of every child mutation: recompute {@code total_amount} from the current
     * line items and persist the aggregate.
     */
    public void saveWithTotals(InvoiceEntity invoice) {
        calculator.applyTotals(invoice);
        repository.save(invoice);
    }

    /** Locates a child row inside the invoice's own collection, or fails with the given message. */
    public <T extends BaseEntity> T findChild(Collection<T> children, UUID childId, String messageKey) {
        return Optional.ofNullable(children).orElseGet(Collections::emptyList).stream()
                .filter(child -> child.getId().equals(childId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(simpleMessage(messageKey)));
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
}

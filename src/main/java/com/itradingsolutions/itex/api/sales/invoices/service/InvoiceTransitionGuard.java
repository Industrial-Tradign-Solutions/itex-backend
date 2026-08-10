package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.InvalidInvoiceTransitionException;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoicePaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Single authority over {@code status} changes (invoicing guide §4): no service writes the field
 * without passing through here first, so the transition matrix lives in one place instead of being
 * re-derived by each endpoint.
 *
 * <p>Manual transitions ({@code ISSUE}, {@code REVERT_TO_DRAFT}, {@code CANCEL}) and payment-derived
 * ones ({@code PARTIAL_PAID}, {@code PAID}, and the walk back on a void) share the same matrix — a
 * derived status is still a transition and gets validated identically.</p>
 */
@Component
@RequiredArgsConstructor
public class InvoiceTransitionGuard extends UtilServiceAbs {

    private static final Map<InvoiceStatus, Set<InvoiceStatus>> ALLOWED = new EnumMap<>(Map.of(
            InvoiceStatus.DRAFT, Set.of(InvoiceStatus.ISSUED, InvoiceStatus.CANCELLED),
            InvoiceStatus.ISSUED, Set.of(InvoiceStatus.DRAFT, InvoiceStatus.PARTIAL_PAID, InvoiceStatus.PAID, InvoiceStatus.CANCELLED),
            InvoiceStatus.PARTIAL_PAID, Set.of(InvoiceStatus.ISSUED, InvoiceStatus.PAID),
            InvoiceStatus.PAID, Set.of(InvoiceStatus.ISSUED, InvoiceStatus.PARTIAL_PAID),
            InvoiceStatus.CANCELLED, Set.of()
    ));

    private final IInvoicePaymentRepository paymentRepository;

    /**
     * Moving to the status the invoice already holds is a no-op, not an error: a second payment on
     * an already {@code PARTIAL_PAID} invoice recalculates the same status.
     */
    public void assertCanTransition(InvoiceEntity invoice, InvoiceStatus target) {
        var current = invoice.getStatus();
        if (current == target)
            return;

        if (!ALLOWED.getOrDefault(current, Set.of()).contains(target))
            throw new InvalidInvoiceTransitionException(compositeMessage(
                    "sales.invoice.invalid-transition",
                    new String[]{current.getName(), target.getName()}
            ));
    }

    /**
     * Explicit safeguard the guide §4 asks for on cancel and revert: an invoice with real money
     * against it is corrected with a credit memo, never by rewinding the document. Checked against
     * {@code t_invoice_payments} rather than inferred from {@code status}, so the rule survives a
     * future change to how statuses are derived.
     */
    public void assertNoPayments(UUID invoiceId) {
        if (paymentRepository.existsByInvoice_IdAndVoidedFalse(invoiceId))
            throw new InvalidInvoiceTransitionException(simpleMessage("sales.invoice.has-payments"));
    }
}

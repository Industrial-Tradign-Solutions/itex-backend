package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceMoney;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoicePaymentRepository;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Money already collected and the status that follows from it (invoicing guide §7 step 5). Sibling
 * of {@link InvoiceAmountCalculator}: that one owns {@code total_amount}, this one owns
 * {@code paid_amount} and the payment-derived statuses, so no endpoint derives them on its own.
 *
 * <p>Both registering and voiding a payment call {@link #apply}; the status therefore walks forward
 * and backward through the same code — {@code PAID → PARTIAL_PAID → ISSUED} on a void is just the
 * recalculation landing on a smaller number.</p>
 */
@Component
@RequiredArgsConstructor
public class InvoiceBalanceCalculator extends UtilServiceAbs {

    private final IInvoicePaymentRepository paymentRepository;
    private final InvoiceTransitionGuard transitionGuard;

    public void apply(InvoiceEntity invoice) {
        var paid = paidAmount(invoice.getId());
        invoice.setPaidAmount(paid);

        var total = InvoiceMoney.orZero(invoice.getTotalAmount());
        var now = ZonedDateTime.now(zoneId);

        if (paid.compareTo(BigDecimal.ZERO) <= 0) {
            transition(invoice, InvoiceStatus.ISSUED);
            invoice.setPartialPaidAt(null);
            invoice.setPaidAt(null);
            return;
        }

        if (paid.compareTo(total) >= 0) {
            transition(invoice, InvoiceStatus.PAID);
            if (invoice.getPaidAt() == null)
                invoice.setPaidAt(now);
            // A settled invoice is no longer collectable, whatever its due date said.
            invoice.setOverdue(false);
            invoice.setOverdueNotifiedAt(null);
            return;
        }

        transition(invoice, InvoiceStatus.PARTIAL_PAID);
        // When it started being partially paid, not when the latest instalment arrived.
        if (invoice.getPartialPaidAt() == null)
            invoice.setPartialPaidAt(now);
        invoice.setPaidAt(null);
    }

    /** Outstanding balance: what the invoice totals minus what has actually been collected. */
    public BigDecimal balance(InvoiceEntity invoice) {
        return InvoiceMoney.scaled(InvoiceMoney.orZero(invoice.getTotalAmount()).subtract(paidAmount(invoice.getId())));
    }

    private BigDecimal paidAmount(UUID invoiceId) {
        return InvoiceMoney.scaled(paymentRepository.sumNotVoidedByInvoiceId(invoiceId));
    }

    private void transition(InvoiceEntity invoice, InvoiceStatus target) {
        transitionGuard.assertCanTransition(invoice, target);
        invoice.setStatus(target);
    }
}

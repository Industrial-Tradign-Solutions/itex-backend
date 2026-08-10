package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * Derives {@code due_at} from the issue date and the payment terms, per the invoicing guide §6.
 *
 * <p>The due date is computed once, when the invoice moves to {@code ISSUED}, and persisted; a
 * re-issue after a revert recomputes it from the new issue date rather than keeping the old value.
 * Terms that express a condition instead of a period ({@code ADVANCED}, {@code PRIOR_TO_SHIPMENT},
 * {@code W_DOCUMENTS}, {@code TO_BE_AGREED}) yield {@code null}, which keeps the invoice out of the
 * overdue sweep until someone fixes a date manually.</p>
 *
 * <p>{@code COD} is treated as {@code ON_ISSUE}: the guide asks for the estimated delivery date and
 * falls back to the issue date, and the invoice carries no delivery estimate.</p>
 */
@Component
public class InvoiceDueDateCalculator {

    public ZonedDateTime calculate(ZonedDateTime issuedAt, PaymentTerms terms) {
        if (issuedAt == null || terms == null || !terms.isCalculable())
            return null;

        return switch (terms.getRule()) {
            case NET_DAYS -> issuedAt.plusDays(terms.getValue());
            case PROX_DAY -> proxDay(issuedAt, terms.getValue());
            case END_OF_MONTH_PLUS_DAYS -> issuedAt.with(TemporalAdjusters.lastDayOfMonth()).plusDays(terms.getValue());
            case ON_ISSUE -> issuedAt;
            case NOT_CALCULABLE -> null;
        };
    }

    /**
     * Day {@code dayOfMonth} of the month following the issue date. Short months are clamped to
     * their last day, so a "NET 30TH PROX" issued in January falls due on Feb 28th (or 29th).
     */
    private ZonedDateTime proxDay(ZonedDateTime issuedAt, int dayOfMonth) {
        var nextMonth = issuedAt.plusMonths(1);
        return nextMonth.withDayOfMonth(Math.min(dayOfMonth, nextMonth.toLocalDate().lengthOfMonth()));
    }
}

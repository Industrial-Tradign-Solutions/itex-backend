package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceMoney;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.response.AgingBucketResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Splits an outstanding balance by age, the way an accounts-receivable team reads it (invoicing
 * guide §11). Sibling of {@link InvoiceAmountCalculator} and {@link InvoiceBalanceCalculator}: it
 * only aggregates what those two already computed, and never writes anything.
 *
 * <p>An invoice with no {@code due_at} — payment terms that express a condition rather than a
 * period — counts as {@code current}: nothing can be overdue without a date to be late against.</p>
 */
@Component
public class InvoiceAgingCalculator extends UtilServiceAbs {

    public AgingBucketResponse aging(List<InvoiceEntity> invoices) {
        var now = ZonedDateTime.now(zoneId);
        var current = BigDecimal.ZERO;
        var days1To30 = BigDecimal.ZERO;
        var days31To60 = BigDecimal.ZERO;
        var days61To90 = BigDecimal.ZERO;
        var days90Plus = BigDecimal.ZERO;

        for (var invoice : invoices) {
            var balance = balanceOf(invoice);
            if (balance.compareTo(BigDecimal.ZERO) <= 0)
                continue;

            long overdueDays = overdueDays(invoice, now);
            if (overdueDays <= 0) current = current.add(balance);
            else if (overdueDays <= 30) days1To30 = days1To30.add(balance);
            else if (overdueDays <= 60) days31To60 = days31To60.add(balance);
            else if (overdueDays <= 90) days61To90 = days61To90.add(balance);
            else days90Plus = days90Plus.add(balance);
        }

        return new AgingBucketResponse(
                scaled(current), scaled(days1To30), scaled(days31To60), scaled(days61To90), scaled(days90Plus));
    }

    private BigDecimal balanceOf(InvoiceEntity invoice) {
        return InvoiceMoney.orZero(invoice.getTotalAmount()).subtract(InvoiceMoney.orZero(invoice.getPaidAmount()));
    }

    private long overdueDays(InvoiceEntity invoice, ZonedDateTime now) {
        if (invoice.getDueAt() == null)
            return 0;
        return ChronoUnit.DAYS.between(invoice.getDueAt(), now);
    }

    private BigDecimal scaled(BigDecimal value) {
        return InvoiceMoney.scaled(value);
    }
}

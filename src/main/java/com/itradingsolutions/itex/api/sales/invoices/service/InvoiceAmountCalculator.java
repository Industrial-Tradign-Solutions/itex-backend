package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceMoney;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceChargeEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceIpProductEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceTaxEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Money math for Invoice, per the invoicing guide §5: internal scale is always 5 decimals,
 * HALF_UP; rounding to 2 decimals is exclusively a PDF-presentation concern and never happens
 * here.
 *
 * <p>Taxes whose taxable base should be recomputed from products/charges before the grand total
 * (guide §5, last sentence) are intentionally not implemented — guide §13 marks that rule as
 * pending definition with the business. {@code tax.getValue()} is summed as-is.</p>
 *
 * <p>{@link #productsTotal} sums {@code invoice.getProducts()}, which today is only ever the IP
 * line items (department-scoped product sourcing lives in {@link InvoiceDepartmentDetailResolver}
 * / {@code InvoiceIpDetailResolver}). Once a second department persists its lines somewhere other
 * than {@code t_invoice_ip_products}, this method needs to go through the same per-department
 * resolver instead of reading the entity's {@code products} association directly.</p>
 */
@Component
public class InvoiceAmountCalculator {

    /**
     * Single recalculation point (guide §5/§8): every product/charge/tax mutation calls this before
     * saving, so the persisted {@code total_amount} snapshot never drifts from the line items. It is
     * also the only public entry point — the total is written here or nowhere.
     */
    public void applyTotals(InvoiceEntity invoice) {
        invoice.setTotalAmount(totalAmount(invoice));
    }

    private BigDecimal totalAmount(InvoiceEntity invoice) {
        return InvoiceMoney.scaled(productsTotal(invoice).add(chargesTotal(invoice)).add(taxesTotal(invoice)));
    }

    private BigDecimal productsTotal(InvoiceEntity invoice) {
        return sum(invoice.getProducts(), this::lineSubtotal);
    }

    private BigDecimal chargesTotal(InvoiceEntity invoice) {
        return sum(invoice.getCharges(), InvoiceChargeEntity::getValue);
    }

    private BigDecimal taxesTotal(InvoiceEntity invoice) {
        return sum(invoice.getTaxes(), InvoiceTaxEntity::getValue);
    }

    private <T> BigDecimal sum(List<T> items, Function<T, BigDecimal> amount) {
        return InvoiceMoney.scaled(Optional.ofNullable(items)
                .orElseGet(Collections::emptyList).stream()
                .map(amount)
                .map(InvoiceMoney::orZero)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    /**
     * {@code unitPrice} is the raw cost and {@code profitMargin} the margin, stored as a direct
     * percentage (10.00 = 10%); the billed selling price is recomputed once as
     * {@code cost * (1 + margin / 100)} — the margin is never applied twice (matches
     * {@code InvoiceProductDTO.getExtendedPrice}, both through {@link InvoiceMoney}).
     */
    private BigDecimal lineSubtotal(InvoiceIpProductEntity product) {
        return InvoiceMoney.extendedPrice(product.getQuantity(), product.getUnitPrice(), product.getProfitMargin());
    }
}

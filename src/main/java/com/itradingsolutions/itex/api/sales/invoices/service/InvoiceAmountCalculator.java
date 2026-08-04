package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceChargeEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceIpProductEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceTaxEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Optional;

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

    private static final int SCALE = 5;

    public BigDecimal totalAmount(InvoiceEntity invoice) {
        return productsTotal(invoice).add(chargesTotal(invoice)).add(taxesTotal(invoice)).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Single recalculation point (guide §5/§8): every product/charge/tax mutation calls this before
     * saving, so the persisted {@code total_amount} snapshot never drifts from the line items.
     */
    public void applyTotals(InvoiceEntity invoice) {
        invoice.setTotalAmount(totalAmount(invoice));
    }

    public BigDecimal productsTotal(InvoiceEntity invoice) {
        return Optional.ofNullable(invoice.getProducts())
                .orElseGet(Collections::emptyList).stream()
                .map(this::lineSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal chargesTotal(InvoiceEntity invoice) {
        return Optional.ofNullable(invoice.getCharges())
                .orElseGet(Collections::emptyList).stream()
                .map(InvoiceChargeEntity::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal taxesTotal(InvoiceEntity invoice) {
        return Optional.ofNullable(invoice.getTaxes())
                .orElseGet(Collections::emptyList).stream()
                .map(InvoiceTaxEntity::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * {@code unitPrice} is the raw cost and {@code profitMargin} the margin; the billed selling
     * price is recomputed once as {@code cost * (1 + margin)} — the margin is never applied twice
     * (matches {@code InvoiceProductDTO.getExtendedPrice}).
     */
    private BigDecimal lineSubtotal(InvoiceIpProductEntity product) {
        var margin = product.getProfitMargin() != null ? product.getProfitMargin() : BigDecimal.ZERO;
        return product.getQuantity()
                .multiply(product.getUnitPrice())
                .multiply(BigDecimal.ONE.add(margin))
                .setScale(SCALE, RoundingMode.HALF_UP);
    }
}

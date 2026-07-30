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
 */
@Component
public class InvoiceAmountCalculator {

    private static final int SCALE = 5;

    public BigDecimal totalAmount(InvoiceEntity invoice) {
        return productsTotal(invoice).add(chargesTotal(invoice)).add(taxesTotal(invoice)).setScale(SCALE, RoundingMode.HALF_UP);
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

    private BigDecimal lineSubtotal(InvoiceIpProductEntity product) {
        return product.getQuantity().multiply(product.getUnitPrice()).setScale(SCALE, RoundingMode.HALF_UP);
    }
}

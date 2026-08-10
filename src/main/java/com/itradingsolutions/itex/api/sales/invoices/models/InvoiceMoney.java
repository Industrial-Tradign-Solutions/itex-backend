package com.itradingsolutions.itex.api.sales.invoices.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The module's money arithmetic in one place (invoicing guide §5): scale 5, {@code HALF_UP},
 * rounding to 2 decimals belongs exclusively to the PDF report DTOs. Lives in {@code models} because
 * both the service layer and the DTOs (which compute derived amounts) depend on it.
 */
public final class InvoiceMoney {

    /** Internal scale of every persisted or derived amount. */
    public static final int SCALE = 5;

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private InvoiceMoney() {}

    public static BigDecimal scaled(BigDecimal value) {
        return orZero(value).setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal orZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    /**
     * Selling price of one unit: cost plus margin. {@code profitMargin} is a direct percentage
     * (10.00 = 10%), so the factor is {@code 1 + margin/100}.
     */
    public static BigDecimal sellingUnitPrice(BigDecimal unitPrice, BigDecimal profitMargin) {
        return scaled(orZero(unitPrice).multiply(sellingFactor(profitMargin)));
    }

    /** Billed amount of a product line: {@code quantity * unitPrice * (1 + margin/100)}. */
    public static BigDecimal extendedPrice(BigDecimal quantity, BigDecimal unitPrice, BigDecimal profitMargin) {
        return scaled(orZero(quantity).multiply(orZero(unitPrice)).multiply(sellingFactor(profitMargin)));
    }

    private static BigDecimal sellingFactor(BigDecimal profitMargin) {
        return BigDecimal.ONE.add(orZero(profitMargin).divide(HUNDRED, 10, RoundingMode.HALF_UP));
    }
}

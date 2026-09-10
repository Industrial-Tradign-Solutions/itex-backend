package com.itradingsolutions.itex.api.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Formatting helpers for report (PDF) generation.
 * <p>
 * System-wide {@code BigDecimal} rule: monetary, quantity and weight values are
 * stored at 5 decimals ({@code numeric(15,5)}) and returned raw (unrounded) to
 * API consumers. Reports, however, display them at <strong>2 decimals</strong>,
 * rounded with strict {@link RoundingMode#HALF_UP} via
 * {@link BigDecimal#setScale(int, RoundingMode)} <em>before</em> applying the
 * display pattern. Relying on {@link DecimalFormat} alone would round with
 * {@link RoundingMode#HALF_EVEN} (banker's rounding), which is laxer and
 * produces cent-level differences against business expectations.
 * </p>
 */
public final class ReportFormatUtil {

    /** Display scale used only by PDF reports; storage and API keep 5 decimals. */
    private static final int PDF_DISPLAY_SCALE = 2;
    private static final String PDF_DISPLAY_PATTERN = "#,##0.00";

    private ReportFormatUtil() {
    }

    /**
     * Formats a monetary value at the PDF display scale (2 decimals, {@code HALF_UP}).
     *
     * @param value the raw value (may be {@code null})
     * @return the formatted string, e.g. {@code "1,150.00"}
     */
    public static String money(BigDecimal value) {
        return format(value);
    }

    /**
     * Formats a weight measurement at the PDF display scale (2 decimals, {@code HALF_UP}).
     *
     * @param value the raw value (may be {@code null})
     * @return the formatted string
     */
    public static String weight(BigDecimal value) {
        return format(value);
    }

    /**
     * Formats a unit price at the PDF display scale (2 decimals, {@code HALF_UP}).
     *
     * @param value the raw value (may be {@code null})
     * @return the formatted string, e.g. {@code "115.00"}
     */
    public static String price(BigDecimal value) {
        return format(value);
    }

    /**
     * Formats a quantity at the PDF display scale (2 decimals, {@code HALF_UP}).
     *
     * @param value the raw value (may be {@code null})
     * @return the formatted string
     */
    public static String quantity(BigDecimal value) {
        return format(value);
    }

    private static String format(BigDecimal value) {
        var safe = value != null ? value : BigDecimal.ZERO;
        var rounded = safe.setScale(PDF_DISPLAY_SCALE, RoundingMode.HALF_UP);
        return new DecimalFormat(PDF_DISPLAY_PATTERN).format(rounded);
    }
}

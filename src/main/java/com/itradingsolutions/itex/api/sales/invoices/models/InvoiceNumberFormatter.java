package com.itradingsolutions.itex.api.sales.invoices.models;

/**
 * Zero-padded presentation of invoice numbers. Numbers persist as {@code BIGINT}; the padding is
 * presentation only (guide §8) and its width is defined exactly once, here.
 */
public final class InvoiceNumberFormatter {

    private static final String PATTERN = "%06d";

    private InvoiceNumberFormatter() {}

    public static String format(Long number) {
        return number != null ? String.format(PATTERN, number) : null;
    }

    /** The official number once issued, the draft number before that. */
    public static String officialOrDraft(Long number, Long draftNumber) {
        return format(number != null ? number : draftNumber);
    }
}

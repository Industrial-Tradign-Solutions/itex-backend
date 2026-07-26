package com.itradingsolutions.itex.api.common.invoiceconsecutive.models.enums;

/**
 * Type of invoice consecutive sequence.
 *
 * <p>Invoice numbering is handled by a dedicated mechanism, independent of the generic
 * {@code itex_consecutive} used by QR/PO/Q. There are two global sequences:</p>
 * <ul>
 *     <li>{@link #DRAFT}: temporary number assigned while the invoice is a draft. It starts at 1
 *     and reuses the lowest released gap, so no number is ever skipped.</li>
 *     <li>{@link #FINAL}: definitive number assigned when the invoice is issued. It starts at a
 *     configurable value and only grows (never reused, never skipped).</li>
 * </ul>
 */
public enum InvoiceConsecutiveType {
    DRAFT, FINAL
}

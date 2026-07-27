package com.itradingsolutions.itex.api.common.salesconsecutive.models.enums;

/**
 * Type of sales consecutive sequence (document + phase).
 *
 * <p>Sales-document numbering is handled by a dedicated mechanism, independent of the generic
 * {@code itex_consecutive} used by QR/PO/Q. Each value is an independent global sequence with the
 * same allocation logic (high-water counter + free list for gap reuse):</p>
 * <ul>
 *     <li>{@link #DRAFT_INV} / {@link #DRAFT_MEMO}: temporary number assigned while the document is a
 *     draft. Starts at 1 and reuses the lowest released gap, so no number is ever skipped.</li>
 *     <li>{@link #INV} / {@link #MEMO}: definitive number assigned when the document is issued. Starts
 *     at a configurable value and only grows. It is never released, so in practice it behaves as a
 *     pure {@code max + 1} counter.</li>
 * </ul>
 *
 * <p>The enum names must match exactly the {@code type} values seeded in
 * {@code t_sales_consecutive_sequence}.</p>
 */
public enum SalesConsecutiveType {
    DRAFT_INV,
    INV,
    DRAFT_MEMO,
    MEMO
}

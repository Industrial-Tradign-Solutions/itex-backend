package com.itradingsolutions.itex.api.common.invoiceconsecutive.services;

/**
 * Allocates the two invoice consecutive numbers (DRAFT and FINAL).
 *
 * <p>This service is exclusive to invoices and, by construction, is decoupled from the generic
 * {@code itex_consecutive} used by QR/PO/Q. Both sequences are global (not segmented by
 * client/year/month). Because there is no department parameter, the rule "invoice consecutives only
 * belong to the ACC department" is enforced structurally — they cannot be generated for any other
 * department.</p>
 */
public interface IInvoiceConsecutiveService {

    /**
     * Allocates the next DRAFT number: reuses the lowest released gap if any, otherwise grows the
     * counter. Never skips a number. Must be paired with {@link #releaseDraft(long)} when a draft
     * invoice is deleted.
     *
     * @return the assigned draft number
     */
    long generateDraft();

    /**
     * Allocates the next FINAL number ({@code max + 1}), starting from the seeded initial value.
     * Never reuses nor skips. A re-issued invoice keeps its already assigned number and must not call
     * this method again.
     *
     * @return the assigned final number
     */
    long generateFinal();

    /**
     * Releases a DRAFT number so it becomes available for reuse. Called when a draft invoice is
     * deleted. Idempotent: releasing an already-released number has no effect.
     *
     * @param number the draft number to release
     */
    void releaseDraft(long number);
}

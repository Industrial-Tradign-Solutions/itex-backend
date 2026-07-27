package com.itradingsolutions.itex.api.common.salesconsecutive.services;

import com.itradingsolutions.itex.api.common.salesconsecutive.models.enums.SalesConsecutiveType;

/**
 * Allocates the sales-document consecutive numbers (invoice and credit memo, draft and final).
 *
 * <p>Internal functionality — there is no REST API; it is called only from the invoice and memo
 * services. All sequences share the same allocation logic: reuse the lowest released gap of the type,
 * otherwise grow the counter. Never skips a number. Independent of the generic {@code itex_consecutive}
 * used by QR/PO/Q.</p>
 */
public interface ISalesConsecutiveService {

    /**
     * Allocates the next number for the given sequence type: reuses the lowest released gap if any,
     * otherwise grows the counter. Draft types must pair each allocation with {@link #release} when the
     * draft document is deleted; final types are never released.
     *
     * @param type sequence type to allocate from
     * @return the assigned number
     */
    long generate(SalesConsecutiveType type);

    /**
     * Releases a number so it becomes available for reuse within its type. Called when a draft document
     * is deleted. Idempotent: releasing an already-released number has no effect.
     *
     * @param type   sequence type the number belongs to
     * @param number the number to release
     */
    void release(SalesConsecutiveType type, long number);
}

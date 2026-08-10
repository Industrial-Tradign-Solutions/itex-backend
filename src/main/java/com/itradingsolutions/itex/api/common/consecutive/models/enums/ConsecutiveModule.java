package com.itradingsolutions.itex.api.common.consecutive.models.enums;

/**
 * Module a document belongs to. Used both as part of the generic consecutive key and as the folder
 * that groups generated PDFs under the data root.
 *
 * <p>{@code INV} is only ever the second of those: invoices number themselves through
 * {@code SalesConsecutiveType}, not through the generic consecutive mechanism.</p>
 */
public enum ConsecutiveModule {
    QR, PO, Q, INV
}

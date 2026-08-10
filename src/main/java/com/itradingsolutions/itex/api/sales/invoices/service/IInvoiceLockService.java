package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;

import java.util.List;
import java.util.UUID;

public interface IInvoiceLockService {

    InvoiceLockResult openAndLock(UUID id, OpenAndLockType type);

    void unlock(UUID id);

    List<UUID> closeAllOpenByUser(String username);

    /**
     * Releases every open Invoice, whoever holds it. Meant for the nightly job: a user who never
     * closes a tab would otherwise keep the document blocked for the rest of the team indefinitely.
     */
    List<UUID> unlockAllOpen();

    /**
     * Enforces {@code itex.tabs.max-tabs-open}: creating, cloning or opening an invoice all consume
     * a tab slot, so the three flows share this single check.
     */
    void assertCanOpenAnother(UUID userId);
}

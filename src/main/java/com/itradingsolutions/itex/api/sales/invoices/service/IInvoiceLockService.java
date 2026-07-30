package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;

import java.util.List;
import java.util.UUID;

public interface IInvoiceLockService {

    InvoiceLockResult openAndLock(UUID id, OpenAndLockType type);

    void unlock(UUID id);

    List<UUID> closeAllOpenByUser(String username);
}

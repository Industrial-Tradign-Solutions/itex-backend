package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.InvoiceMaxOpenException;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.NotExistInvoiceException;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceLockService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAccessGuard;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceDetailResolver;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceLockResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceLockServiceImpl extends UtilServiceAbs implements IInvoiceLockService {

    // Statuses that still accept changes (DRAFT/ISSUED are editable per the guide, and ISSUED/
    // PARTIAL_PAID both accept payment registration) — the only ones worth locking. PAID and
    // CANCELLED are immutable, so locking them would only produce orphaned locks.
    private static final Set<InvoiceStatus> LOCKABLE_STATUSES =
            Set.of(InvoiceStatus.DRAFT, InvoiceStatus.ISSUED, InvoiceStatus.PARTIAL_PAID);

    private final IInvoiceRepository repository;
    private final IUserService userService;
    private final InvoiceAccessGuard guard;
    private final InvoiceDetailResolver detailResolver;

    @Override
    @Transactional
    public InvoiceLockResult openAndLock(UUID id, OpenAndLockType type) {
        var invoice = findById(id);
        guard.assertCanAccess(invoice);

        if (type != OpenAndLockType.EDIT)
            return new InvoiceLockResult(detailResolver.resolve(invoice), true);

        if (!LOCKABLE_STATUSES.contains(invoice.getStatus()))
            return new InvoiceLockResult(detailResolver.resolve(invoice), false);

        var user = userService.getUserAuthenticated();
        if (invoice.getOpenBy() == null)
            validateMaxOpen(user.getId());

        int acquired = repository.tryLock(id, user, ZonedDateTime.now(zoneId));
        var current = findById(id);

        boolean isValidOpen = acquired == 1
                || (current.getOpenBy() != null && current.getOpenBy().getId().equals(user.getId()));

        return new InvoiceLockResult(detailResolver.resolve(current), isValidOpen);
    }

    @Override
    @Transactional
    public void unlock(UUID id) {
        repository.batchUnlock(List.of(id));
    }

    @Override
    @Transactional
    public List<UUID> closeAllOpenByUser(String username) {
        var ids = repository.fetchOpenIdsByUsername(username);
        if (!ids.isEmpty())
            repository.batchUnlock(ids);
        return ids;
    }

    private void validateMaxOpen(UUID userId) {
        if (repository.countByOpenUserId(userId) >= maxTabsOpen)
            throw new InvoiceMaxOpenException(compositeMessage("sales.invoice.not-open-max", new String[]{maxTabsOpen.toString()}));
    }

    private InvoiceEntity findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotExistInvoiceException(simpleMessage("sales.invoice.not-exist")));
    }
}

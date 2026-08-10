package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.InvoiceMaxOpenException;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.NotOpenInvoiceException;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceLockService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAccessGuard;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceDetailResolver;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceFinder;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceLockResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceLockServiceImpl extends UtilServiceAbs implements IInvoiceLockService {

    private final IInvoiceRepository repository;
    private final IUserService userService;
    private final InvoiceAccessGuard accessGuard;
    private final InvoiceDetailResolver detailResolver;
    private final InvoiceFinder finder;

    @Override
    @Transactional
    public InvoiceLockResult openAndLock(UUID id, OpenAndLockType type) {
        var invoice = finder.findDetailById(id);
        accessGuard.assertCanAccess(invoice);

        if (type != OpenAndLockType.EDIT)
            return new InvoiceLockResult(detailResolver.resolve(invoice), true);

        // VIEW_ALL_INVOICE expands visibility but not write access: only the assigned sales rep
        // may acquire an EDIT lock.
        accessGuard.assertCanMutate(invoice);

        if (!InvoiceStatus.LOCKABLE.contains(invoice.getStatus()))
            return new InvoiceLockResult(detailResolver.resolve(invoice), false);

        var user = userService.getUserAuthenticated();
        if (invoice.getOpenBy() == null)
            assertCanOpenAnother(user.getId());

        int acquired = repository.tryLock(id, user, ZonedDateTime.now(zoneId));
        var current = finder.findDetailById(id);

        boolean isValidOpen = acquired == 1
                || (current.getOpenBy() != null && current.getOpenBy().getId().equals(user.getId()));

        return new InvoiceLockResult(detailResolver.resolve(current), isValidOpen);
    }

    /**
     * Releasing a lock is personal: closing an invoice that someone else holds would let any user
     * of the module drop a colleague's edit session. Closing an unlocked invoice, or one's own,
     * stays idempotent.
     */
    @Override
    @Transactional
    public void unlock(UUID id) {
        var invoice = finder.findById(id);
        var openBy = invoice.getOpenBy();

        if (openBy != null && !openBy.getId().equals(userService.getUserAuthenticated().getId()))
            throw new NotOpenInvoiceException(
                    compositeMessage("sales.invoice.not-block-by", new String[]{openBy.getFullName()}));

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

    @Override
    @Transactional
    public List<UUID> unlockAllOpen() {
        var ids = repository.fetchAllOpenIds();
        if (!ids.isEmpty())
            repository.batchUnlock(ids);
        return ids;
    }

    @Override
    public void assertCanOpenAnother(UUID userId) {
        if (repository.countByOpenUserId(userId) >= maxTabsOpen)
            throw new InvoiceMaxOpenException(compositeMessage("sales.invoice.not-open-max", new String[]{maxTabsOpen.toString()}));
    }
}

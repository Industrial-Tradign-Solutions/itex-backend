package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.InvoiceNotEditableException;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.NotOpenInvoiceException;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Preconditions every Invoice mutation must satisfy: the invoice is in an editable state, and the
 * caller is the one holding the lock.
 */
@Component
@RequiredArgsConstructor
public class InvoiceMutationGuard extends UtilServiceAbs {

    private final IUserService userService;

    /**
     * {@code DRAFT} is the only editable status. {@code ISSUED} and {@code PARTIAL_PAID} only
     * accept payment registration, and {@code PAID}/{@code CANCELLED} are final.
     */
    public void assertEditable(InvoiceEntity invoice) {
        if (invoice.getStatus() != InvoiceStatus.DRAFT)
            throw new InvoiceNotEditableException(simpleMessage("sales.invoice.not-editable"));
    }

    public void assertLockedByCurrentUser(InvoiceEntity invoice) {
        if (invoice.getOpenBy() == null)
            throw new NotOpenInvoiceException(simpleMessage("sales.invoice.not-block"));

        var userId = userService.getUserAuthenticated().getId();
        if (!invoice.getOpenBy().getId().equals(userId))
            throw new NotOpenInvoiceException(
                    compositeMessage("sales.invoice.not-block-by", new String[]{invoice.getOpenBy().getFullName()}));
    }
}

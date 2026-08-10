package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.salesconsecutive.models.enums.SalesConsecutiveType;
import com.itradingsolutions.itex.api.common.salesconsecutive.services.ISalesConsecutiveService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.InvoiceNotDeletableException;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceClonedRepository;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceHistoryRepository;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceDeleteService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAccessGuard;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceFinder;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceMutationGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceDeleteServiceImpl extends UtilServiceAbs implements IInvoiceDeleteService {

    private final IInvoiceRepository repository;
    private final IInvoiceHistoryRepository historyRepository;
    private final IInvoiceClonedRepository clonedRepository;
    private final InvoiceAccessGuard accessGuard;
    private final InvoiceMutationGuard mutationGuard;
    private final InvoiceFinder finder;
    private final ISalesConsecutiveService salesConsecutiveService;

    @Override
    @Transactional
    public void delete(UUID id) {
        var invoice = finder.findById(id);

        accessGuard.assertCanAccess(invoice);
        accessGuard.assertCanMutate(invoice);
        mutationGuard.assertLockedByCurrentUser(invoice);
        assertDeletable(invoice.getStatus(), invoice.getNumber());

        var draftNumber = invoice.getDraftNumber();

        // Charges, taxes, products, payments and linked POs go with the cascade; these two do not.
        historyRepository.deleteByInvoiceId(id);
        clonedRepository.deleteByCloneInvoiceId(id);
        repository.delete(invoice);

        salesConsecutiveService.release(SalesConsecutiveType.DRAFT_INV, draftNumber);
    }

    private void assertDeletable(InvoiceStatus status, Long number) {
        if (status != InvoiceStatus.DRAFT || number != null)
            throw new InvoiceNotDeletableException(simpleMessage("sales.invoice.not-deletable"));
    }
}

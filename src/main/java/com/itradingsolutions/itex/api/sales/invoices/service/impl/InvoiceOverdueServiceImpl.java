package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.email.service.IMailService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceNumberFormatter;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceOverdueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceOverdueServiceImpl extends UtilServiceAbs implements IInvoiceOverdueService {

    /** Only an issued invoice with money still outstanding can fall overdue. */
    private static final List<InvoiceStatus> COLLECTABLE_STATUSES = List.copyOf(InvoiceStatus.COLLECTABLE);

    private static final String FIRST_NOTICE_KEY = "sales.invoice.overdue";
    private static final String REMINDER_KEY = "sales.invoice.overdue.reminder";

    private final IInvoiceRepository repository;
    private final IMailService mailService;

    /**
     * Clearing runs alongside marking so an invoice that got paid, cancelled or reverted stops
     * being reported as overdue on the very next run, without waiting for anyone to touch it.
     */
    @Override
    @Transactional
    public int refreshOverdueFlags() {
        var now = ZonedDateTime.now(zoneId);
        int marked = repository.markOverdue(now, COLLECTABLE_STATUSES);
        int cleared = repository.clearOverdue(now, COLLECTABLE_STATUSES);
        log.info("Overdue flags refreshed: {} marked, {} cleared", marked, cleared);
        return marked + cleared;
    }

    /**
     * {@code overdueNotifiedAt} is what keeps the alert from repeating every night; it is reset
     * whenever the invoice stops being overdue, so a re-issued invoice can be reported again.
     */
    @Override
    @Transactional
    public int notifyOverdue() {
        int notified = notifyAll(repository.fetchOverdueNotNotified(), FIRST_NOTICE_KEY);
        log.info("Overdue notifications sent: {}", notified);
        return notified;
    }

    /**
     * The invoice stops being reported the moment it is paid or cancelled, because the daily sweep
     * clears {@code overdue} first — this pass only sees what is genuinely still uncollected.
     */
    @Override
    @Transactional
    public int remindOverdue() {
        int notified = notifyAll(repository.fetchOverdue(), REMINDER_KEY);
        log.info("Overdue reminders sent: {}", notified);
        return notified;
    }

    private int notifyAll(List<InvoiceEntity> invoices, String messageKey) {
        var now = ZonedDateTime.now(zoneId);

        invoices.forEach(invoice -> {
            notifySalesRep(invoice, messageKey);
            invoice.setOverdueNotifiedAt(now);
        });

        repository.saveAll(invoices);
        return invoices.size();
    }

    private void notifySalesRep(InvoiceEntity invoice, String messageKey) {
        var salesRep = invoice.getSalesRep();
        if (salesRep == null || salesRep.getEmail() == null || salesRep.getEmail().isBlank()) {
            log.warn("Overdue invoice {} has no sales rep email; notification skipped", invoice.getId());
            return;
        }

        var number = invoice.getNumber() != null ? InvoiceNumberFormatter.format(invoice.getNumber()) : "-";
        var balance = invoice.getTotalAmount().subtract(invoice.getPaidAmount());

        mailService.sendBasic(
                salesRep.getEmail(),
                compositeMessage(messageKey + ".subject", new String[]{number}),
                compositeMessage(messageKey + ".body", new String[]{
                        number, invoice.getClient().getName(), String.valueOf(invoice.getDueAt()), balance.toPlainString()
                }),
                false
        );
    }
}

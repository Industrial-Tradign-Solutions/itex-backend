package com.itradingsolutions.itex.api.sales.invoices.schedulers;

import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceLockService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceOverdueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for automated Invoice maintenance tasks.
 * Delegates all business logic to the invoice services.
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class InvoiceScheduler {

    private final IInvoiceOverdueService overdueService;
    private final IInvoiceLockService lockService;

    /**
     * Unlocks all open Invoices daily at 11:56 PM.
     * This prevents Invoices from being locked indefinitely if users don't close them manually.
     */
    @Scheduled(cron = "0 56 23 * * *")
    public void cronUnlockInvoices() {
        log.info("Starting scheduled unlock of all open Invoices");
        lockService.unlockAllOpen();
        log.info("Finished scheduled unlock of all open Invoices");
    }

    /**
     * Recomputes the overdue flag of every Invoice daily at 11:57 PM, before the notification pass
     * so both work off the same picture.
     */
    @Scheduled(cron = "0 57 23 * * *")
    public void cronRefreshOverdueInvoices() {
        log.info("Starting scheduled refresh of overdue Invoices");
        overdueService.refreshOverdueFlags();
        log.info("Finished scheduled refresh of overdue Invoices");
    }

    /**
     * Notifies the sales rep of each overdue Invoice not yet reported, daily at 11:58 PM.
     */
    @Scheduled(cron = "0 58 23 * * *")
    public void cronNotifyOverdueInvoices() {
        log.info("Starting scheduled notification of overdue Invoices");
        overdueService.notifyOverdue();
        log.info("Finished scheduled notification of overdue Invoices");
    }

    /**
     * Reminds the sales rep every Monday at 9:00 AM of all the Invoices that are still overdue and
     * unpaid, on top of the one-off notice sent the night each Invoice fell due.
     */
    @Scheduled(cron = "0 0 9 * * MON")
    public void cronRemindOverdueInvoices() {
        log.info("Starting weekly reminder of overdue Invoices");
        overdueService.remindOverdue();
        log.info("Finished weekly reminder of overdue Invoices");
    }
}

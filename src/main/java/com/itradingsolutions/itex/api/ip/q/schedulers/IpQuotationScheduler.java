package com.itradingsolutions.itex.api.ip.q.schedulers;

import com.itradingsolutions.itex.api.ip.q.service.IpQuotationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for automated Quotation maintenance tasks.
 * Delegates all business logic to IpQuotationService.
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class IpQuotationScheduler {

    private final IpQuotationService quotationService;

    /**
     * Unlocks all open Quotations daily at 11:53 PM.
     * This prevents Quotations from being locked indefinitely if users don't close them manually.
     */
    @Scheduled(cron = "0 53 23 * * *")
    public void cronUnlockIpQuotation() {
        log.info("Starting scheduled unlock of all open Quotations");
        quotationService.unlockAllOpenQuotations();
        log.info("Finished scheduled unlock of all open Quotations");
    }

    /**
     * Auto-rejects CREATED Quotations older than 45 days daily at 11:54 PM.
     * This helps maintain data hygiene by automatically closing stale quotations.
     */
    @Scheduled(cron = "0 54 23 * * *")
    public void cronRejectOldQuotations() {
        log.info("Starting scheduled auto-reject of old CREATED Quotations");
        quotationService.autoRejectOldCreatedQuotations();
        log.info("Finished scheduled auto-reject of old CREATED Quotations");
    }
}

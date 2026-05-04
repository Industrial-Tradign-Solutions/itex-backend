package com.itradingsolutions.itex.api.ip.q.schedulers;

import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.ip.q.repository.IpQuotationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

/**
 * Scheduler for automated Quotation maintenance tasks.
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class IpQuotationScheduler {

    private final IpQuotationRepository quotationRepository;

    /**
     * Unlocks all open Quotations daily at 11:53 PM.
     * This prevents Quotations from being locked indefinitely if users don't close them manually.
     */
    @Scheduled(cron = "0 53 23 * * *")
    @Transactional
    private void cronUnlockIpQuotation() {
        log.info("Starting scheduled unlock of all open Quotations");
        var openQuotations = quotationRepository.fetchAllOpen();
        
        openQuotations.forEach(quotation -> {
            quotation.setOpenBy(null);
            quotation.setOpenAt(null);
            quotationRepository.save(quotation);
        });
        
        log.info("Unlocked {} Quotations", openQuotations.size());
    }

    /**
     * Auto-rejects CREATED Quotations older than 45 days daily at 11:54 PM.
     * This helps maintain data hygiene by automatically closing stale quotations.
     */
    @Scheduled(cron = "0 54 23 * * *")
    @Transactional
    private void cronRejectOldQuotations() {
        log.info("Starting scheduled auto-reject of old CREATED Quotations");
        var cutoffDate = ZonedDateTime.now().minusDays(45);
        
        var oldQuotations = quotationRepository.findAll().stream()
                .filter(q -> q.getStatus() == IpQuotationStatus.CREATED)
                .filter(q -> q.getCreatedAt().isBefore(cutoffDate))
                .toList();
        
        oldQuotations.forEach(quotation -> {
            quotation.setStatus(IpQuotationStatus.REJECTED);
            quotation.setRejectAt(ZonedDateTime.now());
            quotationRepository.save(quotation);
        });
        
        log.info("Auto-rejected {} old Quotations", oldQuotations.size());
    }
}

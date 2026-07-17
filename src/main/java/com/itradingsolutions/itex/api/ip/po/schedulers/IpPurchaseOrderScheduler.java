package com.itradingsolutions.itex.api.ip.po.schedulers;

import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler for automated Purchase Order maintenance tasks.
 * Delegates all business logic to IIpPurchaseOrderService.
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class IpPurchaseOrderScheduler {

    private final IIpPurchaseOrderService purchaseOrderService;

    /**
     * Unlocks all open Purchase Orders daily at 11:55 PM.
     * This prevents Purchase Orders from being locked indefinitely if users don't close them manually.
     */
    @Scheduled(cron = "0 55 23 * * *")
    public void cronUnlockIpPurchaseOrder() {
        log.info("Starting scheduled unlock of all open Purchase Orders");
        purchaseOrderService.unlockAllOpenIpPurchaseOrders();
        log.info("Finished scheduled unlock of all open Purchase Orders");
    }

    /**
     * Auto-rejects CREATED Purchase Orders older than 45 days daily at 11:56 PM.
     * This helps maintain data hygiene by automatically closing stale purchase orders.
     */
    @Scheduled(cron = "0 56 23 * * *")
    public void cronRejectOldPurchaseOrders() {
        log.info("Starting scheduled auto-reject of old CREATED Purchase Orders");
        purchaseOrderService.autoRejectOldCreatedIpPurchaseOrders();
        log.info("Finished scheduled auto-reject of old CREATED Purchase Orders");
    }
}

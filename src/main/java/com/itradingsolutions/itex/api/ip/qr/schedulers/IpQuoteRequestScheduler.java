package com.itradingsolutions.itex.api.ip.qr.schedulers;

import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class IpQuoteRequestScheduler {

    private final IIpQuoteRequestService ipQuoteRequestService;

    @Scheduled(cron = "30 50 23 * * *")
    private void cronUnlockIpQuoteRequest() {
        var list = ipQuoteRequestService.listAllOpenIpQuoteRequests();
        list.forEach(qr -> ipQuoteRequestService.unlockIpQuoteRequest(qr.getId()));
    }

    @Scheduled(cron = "0 5 0 * * *")
    private void cronAutoRejectStaleIpQuoteRequests() {
        log.info("Iniciando rechazo automatico de Quote Requests vencidas");
        var rejected = ipQuoteRequestService.autoRejectStaleQuoteRequests();
        log.info("Rechazo automatico de Quote Requests finalizado: {} rechazadas", rejected);
    }
}

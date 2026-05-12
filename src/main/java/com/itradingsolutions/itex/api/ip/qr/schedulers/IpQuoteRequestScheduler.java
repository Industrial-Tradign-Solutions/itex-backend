package com.itradingsolutions.itex.api.ip.qr.schedulers;

import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class IpQuoteRequestScheduler {

    private final IIpQuoteRequestService ipQuoteRequestService;

    @Scheduled(cron = "30 50 23 * * *")
    private void cronUnlockIpQuoteRequest() {
        var list = ipQuoteRequestService.listAllOpenIpQuoteRequests();
        list.forEach(qr -> ipQuoteRequestService.unlockIpQuoteRequest(qr.getId()));
    }

    @Scheduled(cron = "30 50 23 * * *")
    private void cronRejectIpQuoteRequestCreated() {
        final ZonedDateTime limit = ZonedDateTime.now().minusDays(45);

        ipQuoteRequestService.listAllQuoteRequestsByStatus(IpQuoteRequestStatus.CREATED)
                .stream()
                .filter(qr -> qr.getCreatedAt().isBefore(limit))
                .forEach(qr -> ipQuoteRequestService.rejectQuoteRequest(qr.getId()));
    }

    @Scheduled(cron = "30 50 23 * * *")
    private void cronRejectIpQuoteRequestSent() {
        final ZonedDateTime limit = ZonedDateTime.now().minusDays(45);

        ipQuoteRequestService.listAllQuoteRequestsByStatus(IpQuoteRequestStatus.SENT)
                .stream()
                .filter(qr -> qr.getSentAt().isBefore(limit))
                .forEach(qr -> ipQuoteRequestService.rejectQuoteRequest(qr.getId()));
    }

    @Scheduled(cron = "30 50 23 * * *")
    private void cronRejectIpQuoteRequestAnswered() {
        final ZonedDateTime limit = ZonedDateTime.now().minusDays(45);

        ipQuoteRequestService.listAllQuoteRequestsByStatus(IpQuoteRequestStatus.ANSWERED)
                .stream()
                .filter(qr -> qr.getAnsweredAt().isBefore(limit))
                .filter(qr -> qr.getListQuotations() != null && !qr.getListQuotations().isEmpty())
                .forEach(qr -> ipQuoteRequestService.rejectQuoteRequest(qr.getId()));
    }

    @Scheduled(cron = "30 55 23 * * *")
    private void cronAnsweredIpQuoteRequest() {
        final ZonedDateTime limit = ZonedDateTime.now().minusDays(45);

        ipQuoteRequestService.listAllQuoteRequestsByStatus(IpQuoteRequestStatus.SENT)
                .stream()
                .filter(qr -> qr.getCreatedAt().isBefore(limit))
                .forEach(qr -> {
                    if (qr.isValidAnswered()) {
                        ipQuoteRequestService.changeStatusQuoteRequest(qr.getId(), IpQuoteRequestStatus.ANSWERED);
                    }
                });
    }
}

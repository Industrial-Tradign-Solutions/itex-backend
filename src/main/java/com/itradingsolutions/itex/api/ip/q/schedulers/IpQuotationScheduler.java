package com.itradingsolutions.itex.api.ip.q.schedulers;

import com.itradingsolutions.itex.api.ip.q.service.IpQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class IpQuotationScheduler {
    private final IpQuotationService quotationService;


    @Scheduled(cron = "0 53 23 * * *")
    private void cronUnlockIpQuoteRequest() {
        var list = quotationService.listAllOpenIpQuotation();
        list.forEach(quotation ->
            quotationService.unlockIpQuotation(quotation.getId())
        );
    }
}

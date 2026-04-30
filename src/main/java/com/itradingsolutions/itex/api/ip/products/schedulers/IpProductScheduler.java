package com.itradingsolutions.itex.api.ip.products.schedulers;

import com.itradingsolutions.itex.api.ip.products.services.IIpProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class IpProductScheduler {
    private final IIpProductService productService;

    @Scheduled(cron = "30 50 23 * * *")
    private void cronUnlockIpProducts() {
        var listProducts = productService.listAllOpenIpProducts();
        listProducts.forEach(product -> productService.unlockIpProduct(product.getId()));
    }
}

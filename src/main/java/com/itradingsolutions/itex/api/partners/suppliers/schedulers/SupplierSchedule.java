package com.itradingsolutions.itex.api.partners.suppliers.schedulers;

import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class SupplierSchedule {

    private final ISupplierService supplierService;

    @Scheduled(cron = "30 50 23 * * *")
    private void cronUnlockSuppliers() {
        var listSuppliers = supplierService.listAllOpenSupplier(null);
        listSuppliers.forEach(supplier -> supplierService.unlockSupplier(supplier.getId()));
    }
}

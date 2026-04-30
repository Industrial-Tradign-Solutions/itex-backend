package com.itradingsolutions.itex.api.common.util.controllers;

import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.HistoryActions;
import com.itradingsolutions.itex.api.common.util.models.enums.Language;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentMethod;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.common.util.models.enums.PhoneType;
import com.itradingsolutions.itex.api.common.util.models.enums.UnitType;
import com.itradingsolutions.itex.api.common.util.models.responses.EnumItem;
import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import com.itradingsolutions.itex.api.ip.products.services.IIpProductService;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestService;
import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;
import com.itradingsolutions.itex.api.partners.clients.services.IClientService;
import com.itradingsolutions.itex.api.partners.suppliers.models.enums.SupplierStatus;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UtilController extends CommonController {

    @GetMapping("/common/static_lists")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Map<String, Object>>> listSystemEnums() {
        List<Map<String, Object>> response = new ArrayList<>();
        response.add(getItem("client_status", getKeyValueList(ClientStatus.class)));
        response.add(getItem("supplier_status", getKeyValueList(SupplierStatus.class)));
        response.add(getItem("language", getKeyValueList(Language.class)));
        response.add(getItem("payment_terms", getKeyValueList(PaymentTerms.class)));
        response.add(getItem("payment_methods", getKeyValueList(PaymentMethod.class)));
        response.add(getItem("phone_type", getKeyValueList(PhoneType.class)));
        response.add(getItem("ip_products_status", getKeyValueList(IpProductStatus.class)));
        response.add(getItem("freight_class", getKeyValueList(FreightClass.class)));
        response.add(getItem("currency", getKeyValueList(Currency.class)));
        response.add(getItem("ip_quote_request_status", getKeyValueList(IpQuoteRequestStatus.class)));
        response.add(getItem("unit_type", getKeyValueList(UnitType.class)));
        response.add(getItem("lead_time", getKeyValueList(LeadTime.class)));
        return ResponseEntity.ok(response);
    }

    private final IClientService clientService;
    private final ISupplierService supplierService;
    private final IIpProductService ipProductService;
    private final IIpQuoteRequestService quoteRequestService;

    @DeleteMapping("/action/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> unlockAllItems(Authentication auth) {
        new Thread(() -> {
            log.info("Unlocking all items...");

            log.info("Unlocking clients by {}", auth.getName());
            var listClients = clientService.listAllOpenClients(auth.getName());
            listClients.forEach(client -> clientService.unlockClient(client.getId()));

            log.info("Unlocking suppliers by {}", auth.getName());
            var listSuppliers = supplierService.listAllOpenSupplier(auth.getName());
            listSuppliers.forEach(supplier -> supplierService.unlockSupplier(supplier.getId()));

            log.info("Unlocking IP Products by {}", auth.getName());
            var listProducts = ipProductService.listAllOpenIpProducts(auth.getName());
            listProducts.forEach(product -> ipProductService.unlockIpProduct(product.getId()));

            log.info("Unlocking IP Quote Requests by {}", auth.getName());
            var listAllQuoteRequests = quoteRequestService.listAllOpenIpQuoteRequest(auth.getName());
            listAllQuoteRequests.forEach(qr -> quoteRequestService.unlockIpQuoteRequest(qr.getId()));

            log.info("Unlocking all items end.");
        }).start();
        historyService.saveHistoryNotData(HistoryActions.LOGOUT, auth.getName());
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> getItem(String name, List<EnumItem> items) {
        Map<String, Object> response = new HashMap<>();
        response.put("name", name);
        response.put("items", items);
        return response;
    }

    private static <E extends Enum<E> & BaseEnum> List<EnumItem> getKeyValueList(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(item -> new EnumItem(item.name(), item.getName()))
                .toList();
    }
}

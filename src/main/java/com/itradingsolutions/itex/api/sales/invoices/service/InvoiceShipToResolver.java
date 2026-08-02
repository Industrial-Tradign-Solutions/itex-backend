package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactEntity;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientInfoDepEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Derives the invoice ship to block from the Client. Pure mapping: it assumes
 * {@link InvoiceClientValidator} already ran and the required data is present.
 */
@Component
public class InvoiceShipToResolver {

    /**
     * Main active contact of the Client for the given department, or {@code null} when the Client
     * has none registered there. {@code getListContacts()} already returns the {@code is_main}
     * contact first.
     */
    public ClientContactEntity defaultContact(ClientEntity client, ConsecutiveDepartment department) {
        return Optional.ofNullable(client.getInfoByDepartment())
                .orElseGet(List::of)
                .stream()
                .filter(info -> info.getDepartment() != null
                        && department.name().equalsIgnoreCase(info.getDepartment().getName()))
                .map(ClientInfoDepEntity::getListContacts)
                .flatMap(List::stream)
                .filter(ClientContactEntity::isActive)
                .findFirst()
                .orElse(null);
    }

    public InvoiceShipToSnapshot resolve(ClientEntity client, ClientContactEntity contact) {
        return new InvoiceShipToSnapshot(
                client.getName(),
                client.getAddress(),
                client.getCity(),
                contact.getListPhones().getFirst().getFullPhone(),
                contact.getName(),
                contact.getEmail()
        );
    }
}

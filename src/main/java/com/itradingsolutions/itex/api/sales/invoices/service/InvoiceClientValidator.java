package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactEntity;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.ClientNotInvoiceableException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Single policy point for "is this Client invoiceable?". A Client missing any of the data the
 * invoice needs cannot produce even a draft, so the check runs at creation time instead of waiting
 * until the invoice is issued.
 * <p>
 * Every missing piece is collected before throwing, so the user fixes the Client in one pass
 * instead of discovering the gaps one at a time.
 */
@Component
public class InvoiceClientValidator extends UtilServiceAbs {

    public void validate(ClientEntity client, ClientContactEntity contact) {
        List<String> missing = new ArrayList<>();

        if (client.getAddress() == null || client.getAddress().isBlank())
            missing.add("address");

        if (client.getCity() == null)
            missing.add("city");

        if (contact == null)
            missing.add("contact");
        else if (contact.getListPhones().isEmpty())
            missing.add("contact phone");

        if (!missing.isEmpty())
            throw new ClientNotInvoiceableException(compositeMessage(
                    "sales.invoice.client-incomplete",
                    new String[]{client.getName(), String.join(", ", missing)}
            ));
    }
}

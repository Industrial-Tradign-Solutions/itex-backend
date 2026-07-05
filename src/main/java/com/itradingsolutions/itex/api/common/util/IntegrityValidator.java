package com.itradingsolutions.itex.api.common.util;

import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;

import java.util.ArrayList;
import java.util.List;

public class IntegrityValidator {

    private IntegrityValidator() {
    }

    public static List<String> validateQuotationIntegrity(IpQuotationEntity quotation) {
        List<String> errors = new ArrayList<>();

        if (quotation.getQuoteRequestsQuotations() == null || quotation.getQuoteRequestsQuotations().isEmpty()) {
            return errors;
        }

        ClientEntity quotationClient = quotation.getClient();
        Currency quotationCurrency = quotation.getCurrency();

        for (var qqr : quotation.getQuoteRequestsQuotations()) {
            IpQuoteRequestEntity qr = qqr.getQuoteRequest();
            if (qr == null) {
                continue;
            }

            String qrNumber = qr.getNumber();
            ClientEntity qrClient = qr.getClient();
            Currency qrCurrency = qr.getCurrency();

            if (qrClient != null && quotationClient != null && !qrClient.getId().equals(quotationClient.getId())) {
                errors.add(String.format(
                    "QR %s tiene problemas de integridad, pertenece a otro cliente (%s), por favor elimínela",
                    qrNumber, qrClient.getName()
                ));
            }

            if (qrCurrency != null && quotationCurrency != null && !qrCurrency.equals(quotationCurrency)) {
                errors.add(String.format(
                    "QR %s tiene problemas de integridad, tiene una moneda diferente (%s), por favor elimínela",
                    qrNumber, qrCurrency.name()
                ));
            }
        }

        return errors;
    }
}
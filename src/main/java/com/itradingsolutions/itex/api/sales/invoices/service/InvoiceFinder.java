package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.NotExistInvoiceException;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Single point for loading an Invoice by id. Every service resolves the entity through here, so the
 * not-found behaviour (and its message) exists exactly once.
 */
@Component
@RequiredArgsConstructor
public class InvoiceFinder extends UtilServiceAbs {

    private final IInvoiceRepository repository;

    public InvoiceEntity findById(UUID id) {
        return orThrow(repository.findById(id));
    }

    /**
     * Loads with the detail {@code @EntityGraph} (client, contact, ship-to city, sales rep, open
     * by), for the paths that immediately map the full detail response or build the PDF model —
     * one query instead of a lazy-load per association.
     */
    public InvoiceEntity findDetailById(UUID id) {
        return orThrow(repository.fetchDetailById(id));
    }

    /**
     * Pessimistic row lock held until the transaction ends. Used by payment registration/voiding to
     * serialize the balance check with the recalculation that follows it.
     */
    public InvoiceEntity findByIdForUpdate(UUID id) {
        return orThrow(repository.findByIdForUpdate(id));
    }

    private InvoiceEntity orThrow(Optional<InvoiceEntity> invoice) {
        return invoice.orElseThrow(() -> new NotExistInvoiceException(simpleMessage("sales.invoice.not-exist")));
    }
}

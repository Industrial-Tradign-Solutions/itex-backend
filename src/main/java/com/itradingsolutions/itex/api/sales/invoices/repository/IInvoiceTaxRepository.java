package com.itradingsolutions.itex.api.sales.invoices.repository;

import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceTaxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IInvoiceTaxRepository extends JpaRepository<InvoiceTaxEntity, UUID> {

    List<InvoiceTaxEntity> findByInvoice_IdOrderByCreatedAt(UUID invoiceId);
}

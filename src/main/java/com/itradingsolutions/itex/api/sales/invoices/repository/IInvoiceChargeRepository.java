package com.itradingsolutions.itex.api.sales.invoices.repository;

import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceChargeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IInvoiceChargeRepository extends JpaRepository<InvoiceChargeEntity, UUID> {

    List<InvoiceChargeEntity> findByInvoice_IdOrderByCreatedAt(UUID invoiceId);
}

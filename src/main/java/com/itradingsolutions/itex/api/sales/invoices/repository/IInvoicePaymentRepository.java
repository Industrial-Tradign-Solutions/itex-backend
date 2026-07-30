package com.itradingsolutions.itex.api.sales.invoices.repository;

import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoicePaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IInvoicePaymentRepository extends JpaRepository<InvoicePaymentEntity, UUID> {
}

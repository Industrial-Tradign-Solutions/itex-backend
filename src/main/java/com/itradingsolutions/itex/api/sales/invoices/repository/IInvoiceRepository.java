package com.itradingsolutions.itex.api.sales.invoices.repository;

import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IInvoiceRepository extends JpaRepository<InvoiceEntity, UUID>, JpaSpecificationExecutor<InvoiceEntity> {
}

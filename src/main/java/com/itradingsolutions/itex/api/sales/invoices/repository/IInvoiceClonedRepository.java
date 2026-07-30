package com.itradingsolutions.itex.api.sales.invoices.repository;

import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceClonedEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceClonedEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInvoiceClonedRepository extends JpaRepository<InvoiceClonedEntity, InvoiceClonedEntityId> {
}

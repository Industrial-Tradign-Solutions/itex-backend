package com.itradingsolutions.itex.api.sales.invoices.repository;

import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceIpPoEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceIpPoEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInvoiceIpPoRepository extends JpaRepository<InvoiceIpPoEntity, InvoiceIpPoEntityId> {
}

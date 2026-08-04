package com.itradingsolutions.itex.api.sales.invoices.repository;

import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceIpPoEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceIpPoEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IInvoiceIpPoRepository extends JpaRepository<InvoiceIpPoEntity, InvoiceIpPoEntityId> {

    @Query("SELECT l FROM InvoiceIpPoEntity l JOIN FETCH l.purchaseOrder WHERE l.invoice.id = ?1")
    List<InvoiceIpPoEntity> fetchByInvoiceId(UUID invoiceId);
}

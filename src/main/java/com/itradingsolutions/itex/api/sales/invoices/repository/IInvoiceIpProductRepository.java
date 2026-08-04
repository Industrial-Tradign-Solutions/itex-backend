package com.itradingsolutions.itex.api.sales.invoices.repository;

import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceIpProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IInvoiceIpProductRepository extends JpaRepository<InvoiceIpProductEntity, UUID> {

    @Query("SELECT p FROM InvoiceIpProductEntity p JOIN FETCH p.product pr " +
            "LEFT JOIN FETCH pr.brand LEFT JOIN FETCH pr.coo " +
            "WHERE p.invoice.id = ?1 ORDER BY p.number")
    List<InvoiceIpProductEntity> fetchByInvoiceId(UUID invoiceId);
}

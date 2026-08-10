package com.itradingsolutions.itex.api.sales.invoices.repository;

import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoicePaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IInvoicePaymentRepository extends JpaRepository<InvoicePaymentEntity, UUID> {

    // Users come fetch-joined; their departments/role stay lazy on purpose — fetch-joining two List
    // bags (registeredBy.departments + voidedBy.departments) would throw MultipleBagFetchException,
    // and a payment list is short enough for those few extra selects.
    @Query("SELECT p FROM InvoicePaymentEntity p " +
            "LEFT JOIN FETCH p.registeredBy LEFT JOIN FETCH p.voidedBy " +
            "WHERE p.invoice.id = ?1 ORDER BY p.createdAt")
    List<InvoicePaymentEntity> fetchByInvoiceId(UUID invoiceId);

    Optional<InvoicePaymentEntity> findByIdAndInvoice_Id(UUID id, UUID invoiceId);

    /** Money actually collected: voided payments are excluded. Null when the invoice has none. */
    @Query("SELECT SUM(p.amount) FROM InvoicePaymentEntity p WHERE p.invoice.id = ?1 AND p.voided = false")
    BigDecimal sumNotVoidedByInvoiceId(UUID invoiceId);

    boolean existsByInvoice_IdAndVoidedFalse(UUID invoiceId);
}

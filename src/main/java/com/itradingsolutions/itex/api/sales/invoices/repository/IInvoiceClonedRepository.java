package com.itradingsolutions.itex.api.sales.invoices.repository;

import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceClonedEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceClonedEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IInvoiceClonedRepository extends JpaRepository<InvoiceClonedEntity, InvoiceClonedEntityId> {

    /**
     * Finds the clone record where the given invoice is the clone (child). Used to populate the
     * {@code clonedByInvoice} field in the response — returns the parent (original) invoice.
     */
    @Query("SELECT c FROM InvoiceClonedEntity c JOIN FETCH c.mainInvoice WHERE c.id.cloneInvoiceId = ?1")
    Optional<InvoiceClonedEntity> fetchByClonedId(UUID cloneInvoiceId);

    /**
     * Removes the row that records this invoice as somebody else's clone. {@code InvoiceEntity}
     * only cascades the {@code mainInvoice} side, so this end has to be cleared by hand before the
     * invoice can be deleted.
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM InvoiceClonedEntity c WHERE c.id.cloneInvoiceId = ?1")
    void deleteByCloneInvoiceId(UUID cloneInvoiceId);
}

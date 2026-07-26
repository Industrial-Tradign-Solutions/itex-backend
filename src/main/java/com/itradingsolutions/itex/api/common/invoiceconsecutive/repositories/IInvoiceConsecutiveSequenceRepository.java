package com.itradingsolutions.itex.api.common.invoiceconsecutive.repositories;

import com.itradingsolutions.itex.api.common.invoiceconsecutive.models.entities.InvoiceConsecutiveSequence;
import com.itradingsolutions.itex.api.common.invoiceconsecutive.models.enums.InvoiceConsecutiveType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IInvoiceConsecutiveSequenceRepository
        extends JpaRepository<InvoiceConsecutiveSequence, InvoiceConsecutiveType> {

    /**
     * Loads the sequence row for the given type acquiring a pessimistic write lock, so concurrent
     * allocations of the same type are serialized (no duplicated or skipped numbers).
     *
     * @param type sequence type to lock
     * @return the locked sequence row, if it exists
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM InvoiceConsecutiveSequence s WHERE s.type = ?1")
    Optional<InvoiceConsecutiveSequence> findByTypeForUpdate(InvoiceConsecutiveType type);
}

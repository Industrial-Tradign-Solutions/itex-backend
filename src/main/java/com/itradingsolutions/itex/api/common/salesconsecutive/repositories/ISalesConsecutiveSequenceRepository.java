package com.itradingsolutions.itex.api.common.salesconsecutive.repositories;

import com.itradingsolutions.itex.api.common.salesconsecutive.models.entities.SalesConsecutiveSequence;
import com.itradingsolutions.itex.api.common.salesconsecutive.models.enums.SalesConsecutiveType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ISalesConsecutiveSequenceRepository
        extends JpaRepository<SalesConsecutiveSequence, SalesConsecutiveType> {

    /**
     * Loads the sequence row for the given type acquiring a pessimistic write lock, so concurrent
     * allocations of the same type are serialized (no duplicated or skipped numbers).
     *
     * @param type sequence type to lock
     * @return the locked sequence row, if it exists
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SalesConsecutiveSequence s WHERE s.type = ?1")
    Optional<SalesConsecutiveSequence> findByTypeForUpdate(SalesConsecutiveType type);
}

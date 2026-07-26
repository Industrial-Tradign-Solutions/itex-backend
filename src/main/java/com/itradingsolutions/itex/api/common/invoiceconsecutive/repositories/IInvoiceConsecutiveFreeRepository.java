package com.itradingsolutions.itex.api.common.invoiceconsecutive.repositories;

import com.itradingsolutions.itex.api.common.invoiceconsecutive.models.entities.InvoiceConsecutiveFree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IInvoiceConsecutiveFreeRepository extends JpaRepository<InvoiceConsecutiveFree, Long> {

    /**
     * Returns the lowest released DRAFT number available for reuse, or an empty optional when there
     * are no released numbers.
     *
     * @return the smallest number in the free list, if any
     */
    @Query("SELECT MIN(f.number) FROM InvoiceConsecutiveFree f")
    Optional<Long> findMinNumber();
}

package com.itradingsolutions.itex.api.sales.invoices.repository;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface IInvoiceRepository extends JpaRepository<InvoiceEntity, UUID>, JpaSpecificationExecutor<InvoiceEntity> {

    // @EntityGraph on the overridden findAll(Specification, Pageable) is applied as a fetch-graph
    // hint (verified against Spring Data JPA's own test suite, DATAJPA-1207); avoids N+1 on the
    // client/salesRep ManyToOne relations that every list-row mapping touches.
    @Override
    @EntityGraph(attributePaths = {"client", "salesRep"})
    Page<InvoiceEntity> findAll(Specification<InvoiceEntity> spec, Pageable pageable);

    // Atomic lock acquisition: returns 1 if this call took the lock, 0 if someone already had it.
    // Replaces the read-then-write race present in QR/PO's open-lock.
    @Modifying
    @Query("UPDATE InvoiceEntity i SET i.openBy = :user, i.openAt = :now WHERE i.id = :id AND i.openBy IS NULL")
    int tryLock(@Param("id") UUID id, @Param("user") UserEntity user, @Param("now") ZonedDateTime now);

    @Modifying
    @Query("UPDATE InvoiceEntity i SET i.openBy = NULL, i.openAt = NULL WHERE i.id IN (?1)")
    int batchUnlock(List<UUID> ids);

    @Query("SELECT i.id FROM InvoiceEntity i WHERE i.openBy.user = ?1")
    List<UUID> fetchOpenIdsByUsername(String username);

    @Query("SELECT i FROM InvoiceEntity i WHERE i.openBy.user = ?1")
    List<InvoiceEntity> fetchAllOpenByUsername(String username);

    @Query("SELECT COUNT(i.id) FROM InvoiceEntity i WHERE i.openBy.id = ?1")
    int countByOpenUserId(UUID userId);
}

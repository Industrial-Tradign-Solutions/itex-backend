package com.itradingsolutions.itex.api.sales.invoices.repository;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IInvoiceRepository extends JpaRepository<InvoiceEntity, UUID>, JpaSpecificationExecutor<InvoiceEntity> {

    // @EntityGraph on the overridden findAll(Specification, Pageable) is applied as a fetch-graph
    // hint (verified against Spring Data JPA's own test suite, DATAJPA-1207); avoids N+1 on the
    // client/salesRep ManyToOne relations that every list-row mapping touches. NO collection may
    // enter this graph: fetch-joining a bag in a paged query forces Hibernate to paginate in
    // memory (HHH90003004 — every row loaded before slicing the page).
    @Override
    @EntityGraph(attributePaths = {"client", "salesRep"})
    Page<InvoiceEntity> findAll(Specification<InvoiceEntity> spec, Pageable pageable);

    // Unpaged variant for aggregations (client statement): same graph, and being unpaged it is
    // where a caller with a bounded result set belongs.
    @Override
    @EntityGraph(attributePaths = {"client", "salesRep"})
    List<InvoiceEntity> findAll(Specification<InvoiceEntity> spec);

    // Atomic lock acquisition: returns 1 if this call took the lock, 0 if someone already had it.
    // Replaces the read-then-write race present in QR/PO's open-lock. clearAutomatically: the
    // caller re-reads the invoice right after this bulk update, and without it that read would be
    // served stale from the persistence context.
    @Modifying(clearAutomatically = true)
    @Query("UPDATE InvoiceEntity i SET i.openBy = :user, i.openAt = :now WHERE i.id = :id AND i.openBy IS NULL")
    int tryLock(@Param("id") UUID id, @Param("user") UserEntity user, @Param("now") ZonedDateTime now);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE InvoiceEntity i SET i.openBy = NULL, i.openAt = NULL WHERE i.id IN (?1)")
    int batchUnlock(List<UUID> ids);

    @Query("SELECT i.id FROM InvoiceEntity i WHERE i.openBy.user = ?1")
    List<UUID> fetchOpenIdsByUsername(String username);

    // Same N+1 as findAll — mirrors its EntityGraph so /load-open doesn't regress into per-row
    // selects on client/salesRep.
    @EntityGraph(attributePaths = {"client", "salesRep", "salesRep.departments"})
    @Query("SELECT i FROM InvoiceEntity i WHERE i.openBy.user = ?1")
    List<InvoiceEntity> fetchAllOpenByUsername(String username);

    // Detail read: every ManyToOne in one query, deliberately no collections (would collide with
    // salesRep/openBy user department bags via MultipleBagFetchException). Contact phones and user
    // departments resolve as a few extra selects, acceptable because this targets a single invoice.
    @Query("SELECT i FROM InvoiceEntity i WHERE i.id = :id")
    @EntityGraph(attributePaths = {"client", "client.city", "clientContact", "shipToCity",
            "shipToCity.state", "salesRep", "openBy"})
    Optional<InvoiceEntity> fetchDetailById(@Param("id") UUID id);

    @Query("SELECT COUNT(i.id) FROM InvoiceEntity i WHERE i.openBy.id = ?1")
    int countByOpenUserId(UUID userId);

    // Serializes payment registration/voiding on the same invoice: the balance check and the
    // recalculation that follows it are two separate SUMs, so without this lock two concurrent
    // payments could both read the same balance and together overpay the invoice.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InvoiceEntity i WHERE i.id = ?1")
    Optional<InvoiceEntity> findByIdForUpdate(UUID id);

    @Query("SELECT i.id FROM InvoiceEntity i WHERE i.openBy IS NOT NULL")
    List<UUID> fetchAllOpenIds();

    // Overdue sweep (guide §4): is_overdue is a persisted flag so listings can filter on it without
    // comparing dates per row. Both directions are bulk updates — the job touches whole ranges of
    // invoices and has no use for the entities.
    @Modifying
    @Query("UPDATE InvoiceEntity i SET i.overdue = true WHERE i.overdue = false " +
            "AND i.dueAt IS NOT NULL AND i.dueAt < :now AND i.status IN (:collectableStatuses)")
    int markOverdue(@Param("now") ZonedDateTime now, @Param("collectableStatuses") List<InvoiceStatus> collectableStatuses);

    @Modifying
    @Query("UPDATE InvoiceEntity i SET i.overdue = false, i.overdueNotifiedAt = NULL WHERE i.overdue = true " +
            "AND (i.dueAt IS NULL OR i.dueAt >= :now OR i.status NOT IN (:collectableStatuses))")
    int clearOverdue(@Param("now") ZonedDateTime now, @Param("collectableStatuses") List<InvoiceStatus> collectableStatuses);

    @Query("SELECT i FROM InvoiceEntity i WHERE i.overdue = true AND i.overdueNotifiedAt IS NULL")
    @EntityGraph(attributePaths = {"client", "salesRep"})
    List<InvoiceEntity> fetchOverdueNotNotified();

    // Weekly reminder: unlike the first notice, this one ignores overdueNotifiedAt — every invoice
    // still overdue is reported again until it gets paid.
    @Query("SELECT i FROM InvoiceEntity i WHERE i.overdue = true")
    @EntityGraph(attributePaths = {"client", "salesRep"})
    List<InvoiceEntity> fetchOverdue();
}

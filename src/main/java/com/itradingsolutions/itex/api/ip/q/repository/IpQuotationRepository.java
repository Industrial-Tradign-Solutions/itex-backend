package com.itradingsolutions.itex.api.ip.q.repository;

import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IpQuotationRepository extends JpaRepository<IpQuotationEntity, UUID>, JpaSpecificationExecutor<IpQuotationEntity> {

    @Query("SELECT q FROM IpQuotationEntity q JOIN FETCH q.openBy WHERE q.openBy.user=?1")
    List<IpQuotationEntity> fetchAllOpenByUsername(String username);

    @Query("SELECT q FROM IpQuotationEntity q JOIN FETCH q.openBy WHERE q.openBy IS NOT NULL")
    List<IpQuotationEntity> fetchAllOpen();

    @Query("SELECT COUNT(c.id) FROM IpQuotationEntity c WHERE c.openBy.id = ?1")
    int countByOpenUserId(UUID userOpenById);

    @EntityGraph(attributePaths = {"client", "salesRep"})
    Page<IpQuotationEntity> findAll(Specification<IpQuotationEntity> spec, Pageable pageable);

    @Query("SELECT q FROM IpQuotationEntity q WHERE q.id = ?1 AND q.client.id = ?2")
    Optional<IpQuotationEntity> fetchByIdAndClient(UUID id, UUID clientId);

    @Query("""
            SELECT DISTINCT q FROM IpQuotationEntity q
            LEFT JOIN FETCH q.quoteRequestsQuotations qqr
            LEFT JOIN FETCH qqr.quoteRequest
            WHERE q.client.id = ?1 AND q.status IN (?2) AND q.currency = ?3
            ORDER BY q.createdAt DESC
            """)
    List<IpQuotationEntity> fetchByClientAndStatus(UUID clientId, List<IpQuotationStatus> statuses, Currency currency);

    @Query("SELECT q FROM IpQuotationEntity q JOIN FETCH q.salesRep WHERE q.status = ?1 AND q.createdAt < ?2")
    List<IpQuotationEntity> fetchStaleCreated(IpQuotationStatus status, ZonedDateTime cutoff);

    @Query("SELECT q FROM IpQuotationEntity q JOIN FETCH q.salesRep WHERE q.status = ?1 AND q.sentAt IS NOT NULL AND q.sentAt < ?2")
    List<IpQuotationEntity> fetchStaleSent(IpQuotationStatus status, ZonedDateTime cutoff);

    @Query("SELECT q FROM IpQuotationEntity q JOIN FETCH q.salesRep WHERE q.status = ?1 AND q.answeredAt IS NOT NULL AND q.answeredAt < ?2")
    List<IpQuotationEntity> fetchStaleAnswered(IpQuotationStatus status, ZonedDateTime cutoff);

    @Modifying
    @Query("UPDATE IpQuotationEntity q SET q.openBy = NULL, q.openAt = NULL WHERE q.openBy IS NOT NULL")
    int clearAllOpenLocks();
}

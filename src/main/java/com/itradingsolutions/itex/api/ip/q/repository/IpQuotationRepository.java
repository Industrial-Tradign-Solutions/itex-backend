package com.itradingsolutions.itex.api.ip.q.repository;

import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IpQuotationRepository extends JpaRepository<IpQuotationEntity, UUID>, JpaSpecificationExecutor<IpQuotationEntity> {

    @Query("SELECT q FROM IpQuotationEntity q WHERE q.openBy.user=?1")
    List<IpQuotationEntity> fetchAllOpenByUsername(String username);

    @Query("SELECT q FROM IpQuotationEntity q WHERE q.openBy IS NOT NULL")
    List<IpQuotationEntity> fetchAllOpen();

    @Query("SELECT COUNT(c.id) FROM IpQuotationEntity c WHERE c.openBy.id = ?1")
    int countByOpenUserId(UUID userOpenById);

    @Query("SELECT q FROM IpQuotationEntity q WHERE q.id = ?1 AND q.client.id = ?2")
    Optional<IpQuotationEntity> fetchByIdAndClient(UUID id, UUID clientId);

    @Query("""
            SELECT DISTINCT q FROM IpQuotationEntity q
            LEFT JOIN FETCH q.quoteRequestsQuotations qqr
            LEFT JOIN qqr.quotationProducts qp
            LEFT JOIN FETCH qp.quoteRequestProduct qrp
            LEFT JOIN FETCH qrp.ipQuoteRequest qr
            LEFT JOIN FETCH qr.supplier
            WHERE q.client.id = ?1 AND q.status IN (?2) AND q.currency = ?3
            ORDER BY q.createdAt DESC
            """)
    List<IpQuotationEntity> fetchByClientAndStatus(UUID clientId, List<IpQuotationStatus> statuses, Currency currency);
}

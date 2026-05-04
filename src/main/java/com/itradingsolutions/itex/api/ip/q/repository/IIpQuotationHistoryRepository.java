package com.itradingsolutions.itex.api.ip.q.repository;

import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for IP Quotation history records.
 */
@Repository
public interface IIpQuotationHistoryRepository extends JpaRepository<IpQuotationHistoryEntity, UUID> {

    @Query("SELECT h FROM IpQuotationHistoryEntity h WHERE h.ipQuotation = ?1 ORDER BY h.createdAt DESC")
    List<IpQuotationHistoryEntity> fetchByIpQuotationId(UUID ipQuotationId);
}

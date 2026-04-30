package com.itradingsolutions.itex.api.ip.qr.repositories;

import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IIpQuoteRequestHistoryRepository extends JpaRepository<IpQuoteRequestHistoryEntity, UUID> {

    @Query("SELECT h FROM IpQuoteRequestHistoryEntity h WHERE h.ipQuoteRequest = ?1 ORDER BY h.createdAt DESC")
    List<IpQuoteRequestHistoryEntity> fetchByIpQrId(UUID ipQrId);
}

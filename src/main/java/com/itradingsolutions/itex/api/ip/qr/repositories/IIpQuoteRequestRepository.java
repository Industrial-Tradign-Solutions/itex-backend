package com.itradingsolutions.itex.api.ip.qr.repositories;

import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IIpQuoteRequestRepository extends JpaRepository<IpQuoteRequestEntity, UUID>, JpaSpecificationExecutor<IpQuoteRequestEntity> {

    @Query("SELECT COUNT(c.id) FROM IpQuoteRequestEntity c WHERE c.openBy.id = ?1")
    int countByOpenUserId(UUID userOpenById);

    @Query("SELECT c FROM IpQuoteRequestEntity c WHERE c.openBy.user=?1")
    List<IpQuoteRequestEntity> fetchAllOpenByUsername(String username);

    @Query("SELECT c FROM IpQuoteRequestEntity c WHERE c.openBy IS NOT NULL")
    List<IpQuoteRequestEntity> fetchAllOpen();

    @Query("SELECT c FROM IpQuoteRequestEntity c WHERE c.status = ?1")
    List<IpQuoteRequestEntity> fetchAllByStatus(IpQuoteRequestStatus status);

    @Query("SELECT c FROM IpQuoteRequestEntity c WHERE c.client.id = ?1 AND c.status IN (?2) AND c.currency = ?3 ORDER BY c.createdAt DESC")
    List<IpQuoteRequestEntity> fetchAllByClientAndStatus(UUID clientId, List<IpQuoteRequestStatus> status, Currency currency);

    @Query("SELECT c FROM IpQuoteRequestEntity c WHERE c.id = ?1 AND c.client.id = ?2")
    Optional<IpQuoteRequestEntity> fetchAllByIdAndClient(UUID id, UUID clientId);
}

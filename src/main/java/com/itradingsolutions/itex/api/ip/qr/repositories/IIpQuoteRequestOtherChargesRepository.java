package com.itradingsolutions.itex.api.ip.qr.repositories;

import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestOtherChargesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface IIpQuoteRequestOtherChargesRepository extends JpaRepository<IpQuoteRequestOtherChargesEntity, UUID> {

    @Query("SELECT p FROM IpQuoteRequestOtherChargesEntity p WHERE p.id = ?1 AND p.ipQuoteRequest.id = ?2")
    Optional<IpQuoteRequestOtherChargesEntity> fetchOneById(UUID id, UUID qrId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM IpQuoteRequestOtherChargesEntity p WHERE p.description = ?1 AND p.ipQuoteRequest.id = ?2")
    boolean existsDescription(String description, UUID qrId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM IpQuoteRequestOtherChargesEntity p WHERE p.description = ?1 AND p.ipQuoteRequest.id = ?2 AND p.id <> ?3")
    boolean existsDescription(String description, UUID qrId, UUID qrProductId);

    @Modifying
    @Query("DELETE FROM IpQuoteRequestOtherChargesEntity p WHERE p.ipQuoteRequest.id = ?1 AND p.id = ?2")
    void deleteById(UUID qrId, UUID qrProductId);

    @Query("SELECT p FROM IpQuoteRequestOtherChargesEntity p WHERE p.ipQuoteRequest.id IN ?1")
    List<IpQuoteRequestOtherChargesEntity> findByIpQuoteRequestIds(Set<UUID> qrIds);
}

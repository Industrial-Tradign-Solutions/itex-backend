package com.itradingsolutions.itex.api.ip.q.repository;

import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IIpQuotationsQuoteRequestRepository extends JpaRepository<IpQuotationsQuoteRequestEntity, UUID> {

    Optional<IpQuotationsQuoteRequestEntity> findByIdAndQuotation_Id(UUID id, UUID quotationId);

    boolean existsByIdAndQuotation_Id(UUID id, UUID quotationId);
}

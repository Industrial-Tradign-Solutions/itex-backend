package com.itradingsolutions.itex.api.ip.q.repository;

import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IIpQuotationProductRepository extends JpaRepository<IpQuotationProductEntity, UUID> {

    Optional<IpQuotationProductEntity> findByIdAndQuotationsQuoteRequest_Quotation_Id(UUID id, UUID quotationId);

    void deleteByIdAndQuotationsQuoteRequest_Quotation_Id(UUID id, UUID quotationId);

    boolean existsByQuoteRequestProduct_IdAndQuotationsQuoteRequest_Id(UUID qrProductId, UUID qqrId);

    boolean existsByQuoteRequestProduct_IdAndQuotationsQuoteRequest_IdAndIdNot(UUID qrProductId, UUID qqrId, UUID excludeId);
}

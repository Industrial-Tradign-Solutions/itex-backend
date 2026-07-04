package com.itradingsolutions.itex.api.ip.q.repository;

import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IIpQuotationsQuoteRequestRepository extends JpaRepository<IpQuotationsQuoteRequestEntity, UUID> {

    Optional<IpQuotationsQuoteRequestEntity> findByIdAndQuotation_Id(UUID id, UUID quotationId);

    boolean existsByIdAndQuotation_Id(UUID id, UUID quotationId);

    List<IpQuotationsQuoteRequestEntity> findByQuotation_Id(UUID quotationId);

    IpQuotationsQuoteRequestEntity findByQuotation_IdAndQuoteRequest_Id(UUID quotationId, UUID quoteRequestId);

    @Modifying
    @Query("DELETE FROM IpQuotationProductEntity p WHERE p.quotationsQuoteRequest.id = :qqrId")
    int deleteProductsByQqrId(@Param("qqrId") UUID qqrId);

    @Modifying
    @Query("DELETE FROM IpQuotationOtherChargesQuoteRequestEntity oc WHERE oc.quotationsQuoteRequest.id = :qqrId")
    int deleteOtherChargesByQqrId(@Param("qqrId") UUID qqrId);

    @Modifying
    @Query("DELETE FROM IpQuotationsQuoteRequestEntity qqr WHERE qqr.id = :qqrId")
    int deleteQqrById(@Param("qqrId") UUID qqrId);
}

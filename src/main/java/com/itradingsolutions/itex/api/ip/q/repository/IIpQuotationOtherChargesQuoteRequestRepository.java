package com.itradingsolutions.itex.api.ip.q.repository;

import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationOtherChargesQuoteRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface IIpQuotationOtherChargesQuoteRequestRepository extends JpaRepository<IpQuotationOtherChargesQuoteRequestEntity, UUID> {

    Optional<IpQuotationOtherChargesQuoteRequestEntity> findByIdAndQuotationsQuoteRequest_Quotation_Id(UUID id, UUID quotationId);

    @Query("SELECT oc.qrOtherCharge.id FROM IpQuotationOtherChargesQuoteRequestEntity oc WHERE oc.quotationsQuoteRequest.quotation.id = ?1")
    Set<UUID> findImportedQrOtherChargeIdsByQuotationId(UUID quotationId);

    @Query("SELECT oc FROM IpQuotationOtherChargesQuoteRequestEntity oc JOIN FETCH oc.qrOtherCharge WHERE oc.quotationsQuoteRequest.quotation.id = ?1")
    List<IpQuotationOtherChargesQuoteRequestEntity> findAllByQuotationId(UUID quotationId);

    @Modifying
    @Query("DELETE FROM IpQuotationOtherChargesQuoteRequestEntity oc WHERE oc.quotationsQuoteRequest.id = :qqrId")
    int deleteByQqrId(@Param("qqrId") UUID qqrId);

    void deleteByQuotationsQuoteRequest_IdAndId(UUID qqrId, UUID id);

    @Query("SELECT oc.qrOtherCharge.id FROM IpQuotationOtherChargesQuoteRequestEntity oc WHERE oc.quotationsQuoteRequest.id = ?1")
    Set<UUID> findQrOtherChargeIdsByQqrId(UUID qqrId);
}

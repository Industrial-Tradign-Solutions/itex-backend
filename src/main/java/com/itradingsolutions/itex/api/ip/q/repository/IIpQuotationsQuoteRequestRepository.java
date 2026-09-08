package com.itradingsolutions.itex.api.ip.q.repository;

import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
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
public interface IIpQuotationsQuoteRequestRepository extends JpaRepository<IpQuotationsQuoteRequestEntity, UUID> {

    Optional<IpQuotationsQuoteRequestEntity> findByIdAndQuotation_Id(UUID id, UUID quotationId);

    boolean existsByIdAndQuotation_Id(UUID id, UUID quotationId);

    @Query("""
           SELECT qqr FROM IpQuotationsQuoteRequestEntity qqr
           JOIN FETCH qqr.quoteRequest
           WHERE qqr.quotation.id = :quotationId
           """)
    List<IpQuotationsQuoteRequestEntity> findByQuotation_Id(@Param("quotationId") UUID quotationId);

    @Query("""
           SELECT DISTINCT qqr FROM IpQuotationsQuoteRequestEntity qqr
           JOIN FETCH qqr.quoteRequest
           LEFT JOIN FETCH qqr.quotationProducts
           WHERE qqr.quotation.id = :quotationId
           """)
    List<IpQuotationsQuoteRequestEntity> findByQuotationIdWithQuoteRequestsAndProducts(@Param("quotationId") UUID quotationId);

    @Query("""
           SELECT DISTINCT qqr FROM IpQuotationsQuoteRequestEntity qqr
           JOIN FETCH qqr.quoteRequest
           WHERE qqr.quotation.id = :quotationId AND qqr.id IN :ids
           """)
    List<IpQuotationsQuoteRequestEntity> findByQuotationIdAndIdsIn(@Param("quotationId") UUID quotationId,
                                                                   @Param("ids") Set<UUID> ids);

    @Query("""
           SELECT qqr FROM IpQuotationsQuoteRequestEntity qqr
           JOIN FETCH qqr.quoteRequest
           WHERE qqr.quotation.id = :quotationId AND qqr.quoteRequest.id = :quoteRequestId
           """)
    IpQuotationsQuoteRequestEntity findByQuotation_IdAndQuoteRequest_Id(@Param("quotationId") UUID quotationId,
                                                                        @Param("quoteRequestId") UUID quoteRequestId);

    @Modifying
    @Query("DELETE FROM IpQuotationProductEntity p WHERE p.quotationsQuoteRequest.id = :qqrId")
    int deleteProductsByQqrId(@Param("qqrId") UUID qqrId);

    @Modifying
    @Query("DELETE FROM IpQuotationOtherChargesQuoteRequestEntity oc WHERE oc.quotationsQuoteRequest.id = :qqrId")
    int deleteOtherChargesByQqrId(@Param("qqrId") UUID qqrId);

    @Modifying
    @Query("DELETE FROM IpQuotationsQuoteRequestEntity qqr WHERE qqr.id = :qqrId")
    int deleteQqrById(@Param("qqrId") UUID qqrId);

    @Query("""
           SELECT COUNT(qqr)
           FROM IpQuotationsQuoteRequestEntity qqr
           WHERE qqr.quotation.id = :quotationId
             AND qqr.quoteRequest.status NOT IN (:allowedStatuses)
           """)
    long countQuoteRequestsNotInStatuses(@Param("quotationId") UUID quotationId,
                                         @Param("allowedStatuses") List<IpQuoteRequestStatus> allowedStatuses);

    boolean existsByQuoteRequest_Id(UUID quoteRequestId);

    @Query("""
           SELECT COUNT(qqr)
           FROM IpQuotationsQuoteRequestEntity qqr
           WHERE qqr.quoteRequest.id = :quoteRequestId
             AND qqr.quotation.status != :status
           """)
    long countByQuoteRequestIdAndQuotationStatusNot(@Param("quoteRequestId") UUID quoteRequestId,
                                                    @Param("status") IpQuotationStatus status);
}

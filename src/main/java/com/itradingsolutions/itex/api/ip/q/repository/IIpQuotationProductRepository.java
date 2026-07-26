package com.itradingsolutions.itex.api.ip.q.repository;

import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface IIpQuotationProductRepository extends JpaRepository<IpQuotationProductEntity, UUID> {

    Optional<IpQuotationProductEntity> findByIdAndQuotationsQuoteRequest_Quotation_Id(UUID id, UUID quotationId);

    void deleteByIdAndQuotationsQuoteRequest_Quotation_Id(UUID id, UUID quotationId);

    boolean existsByQuoteRequestProduct_IdAndQuotationsQuoteRequest_IdAndIdNot(UUID qrProductId, UUID qqrId, UUID excludeId);

    @Query("""
           SELECT qp.quoteRequestProduct.id FROM IpQuotationProductEntity qp
           JOIN qp.quotationsQuoteRequest qqr
           WHERE qqr.quotation.id = :quotationId
           """)
    Set<UUID> findQuoteRequestProductIdsByQuotationId(@Param("quotationId") UUID quotationId);

    @Query("""
           SELECT DISTINCT qrp.ipProduct.id FROM IpQuotationProductEntity qp
           JOIN qp.quoteRequestProduct qrp
           JOIN qp.quotationsQuoteRequest qqr
           WHERE qqr.quotation.id = :quotationId
           """)
    Set<UUID> findExistingProductIdsByQuotationId(@Param("quotationId") UUID quotationId);

    List<IpQuotationProductEntity> findByQuotationsQuoteRequest_Quotation_IdAndQuotationsQuoteRequest_QuoteRequest_Supplier_Id(
            UUID quotationId, UUID supplierId);
}

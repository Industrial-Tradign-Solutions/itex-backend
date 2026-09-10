package com.itradingsolutions.itex.api.ip.q.repository;

import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import com.itradingsolutions.itex.api.ip.q.models.dto.QuotationProductStatusProjection;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = {
            "quoteRequestProduct",
            "quoteRequestProduct.ipProduct",
            "quotationsQuoteRequest",
            "quotationsQuoteRequest.quoteRequest",
            "quotationsQuoteRequest.quoteRequest.supplier"
    })
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

    @Query("""
           SELECT qp FROM IpQuotationProductEntity qp
           JOIN FETCH qp.quoteRequestProduct qrp
           JOIN FETCH qrp.ipProduct
           JOIN FETCH qp.quotationsQuoteRequest qqr
           JOIN FETCH qqr.quoteRequest
           WHERE qqr.quotation.id = :quotationId AND qqr.quoteRequest.supplier.id = :supplierId
           """)
    List<IpQuotationProductEntity> findByQuotationsQuoteRequest_Quotation_IdAndQuotationsQuoteRequest_QuoteRequest_Supplier_Id(
            @Param("quotationId") UUID quotationId, @Param("supplierId") UUID supplierId);

    @Query("""
           SELECT new com.itradingsolutions.itex.api.ip.q.models.dto.QuotationProductStatusProjection(
               qp.quoteRequestProduct.ipProduct.mfrReference,
               qp.quoteRequestProduct.ipProduct.description,
               qp.quoteRequestProduct.ipProduct.status)
           FROM IpQuotationProductEntity qp
           JOIN qp.quotationsQuoteRequest qqr
           WHERE qqr.quotation.id = :quotationId AND qp.quoteRequestProduct.ipProduct.status <> :status
           """)
    List<QuotationProductStatusProjection> fetchProductsNotInStatus(@Param("quotationId") UUID quotationId,
                                                                    @Param("status") IpProductStatus status);

    @Query("""
           SELECT qp FROM IpQuotationProductEntity qp
           JOIN FETCH qp.quoteRequestProduct qrp
           JOIN FETCH qrp.ipQuoteRequest qr
           JOIN FETCH qr.supplier
           JOIN FETCH qp.quotationsQuoteRequest qqr
           WHERE qqr.quotation.id IN :quotationIds
           """)
    List<IpQuotationProductEntity> fetchByQuotationIds(@Param("quotationIds") List<UUID> quotationIds);
}

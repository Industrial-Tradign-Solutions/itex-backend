package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface IIpPurchaseOrderProductRepository extends JpaRepository<IpPurchaseOrderProductEntity, UUID> {

    Optional<IpPurchaseOrderProductEntity> findByIdAndPurchaseOrder_Id(UUID id, UUID poId);

    @Query("SELECT p.quotationProduct.id FROM IpPurchaseOrderProductEntity p WHERE p.purchaseOrder.id = :poId")
    Set<UUID> findQuotationProductIdsByPurchaseOrderId(@Param("poId") UUID poId);

    @Query("SELECT COALESCE(MAX(p.number), 0) FROM IpPurchaseOrderProductEntity p WHERE p.purchaseOrder.id = :poId")
    int findMaxNumberByPurchaseOrderId(@Param("poId") UUID poId);
}

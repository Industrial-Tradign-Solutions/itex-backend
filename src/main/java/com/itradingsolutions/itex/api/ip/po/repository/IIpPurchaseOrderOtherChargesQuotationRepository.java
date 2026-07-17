package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderOtherChargesQuotationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface IIpPurchaseOrderOtherChargesQuotationRepository extends JpaRepository<IpPurchaseOrderOtherChargesQuotationEntity, UUID> {

    Optional<IpPurchaseOrderOtherChargesQuotationEntity> findByIdAndPurchaseOrder_Id(UUID id, UUID poId);

    @Query("SELECT l.quotationOtherCharge.id FROM IpPurchaseOrderOtherChargesQuotationEntity l WHERE l.purchaseOrder.id = :poId")
    Set<UUID> findImportedChargeIdsByPurchaseOrderId(@Param("poId") UUID poId);
}

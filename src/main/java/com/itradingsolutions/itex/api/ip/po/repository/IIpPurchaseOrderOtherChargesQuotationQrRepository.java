package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderOtherChargesQuotationQrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface IIpPurchaseOrderOtherChargesQuotationQrRepository extends JpaRepository<IpPurchaseOrderOtherChargesQuotationQrEntity, UUID> {

    Optional<IpPurchaseOrderOtherChargesQuotationQrEntity> findByIdAndPurchaseOrder_Id(UUID id, UUID poId);

    @Query("SELECT l.quotationQrOtherCharge.id FROM IpPurchaseOrderOtherChargesQuotationQrEntity l WHERE l.purchaseOrder.id = :poId")
    Set<UUID> findImportedChargeIdsByPurchaseOrderId(@Param("poId") UUID poId);
}

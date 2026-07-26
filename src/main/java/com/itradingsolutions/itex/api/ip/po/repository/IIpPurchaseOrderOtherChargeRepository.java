package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderOtherChargeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IIpPurchaseOrderOtherChargeRepository extends JpaRepository<IpPurchaseOrderOtherChargeEntity, UUID> {

    @Query("SELECT oc FROM IpPurchaseOrderOtherChargeEntity oc WHERE oc.id = ?1 AND oc.purchaseOrder.id = ?2")
    Optional<IpPurchaseOrderOtherChargeEntity> fetchOneById(UUID id, UUID poId);

    @Query("SELECT CASE WHEN COUNT(oc) > 0 THEN TRUE ELSE FALSE END FROM IpPurchaseOrderOtherChargeEntity oc WHERE oc.description = ?1 AND oc.purchaseOrder.id = ?2")
    boolean existsDescription(String description, UUID poId);

    @Query("SELECT CASE WHEN COUNT(oc) > 0 THEN TRUE ELSE FALSE END FROM IpPurchaseOrderOtherChargeEntity oc WHERE oc.description = ?1 AND oc.purchaseOrder.id = ?2 AND oc.id <> ?3")
    boolean existsDescription(String description, UUID poId, UUID otherChargeId);

    @Modifying
    @Query("DELETE FROM IpPurchaseOrderOtherChargeEntity oc WHERE oc.purchaseOrder.id = ?1 AND oc.id = ?2")
    void deleteById(UUID poId, UUID otherChargeId);
}

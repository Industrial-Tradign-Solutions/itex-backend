package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IIpPurchaseOrderRepository extends JpaRepository<IpPurchaseOrderEntity, UUID>, JpaSpecificationExecutor<IpPurchaseOrderEntity> {

    @Query("SELECT COUNT(c.id) FROM IpPurchaseOrderEntity c WHERE c.openBy.id = ?1")
    int countByOpenUserId(UUID userId);

    @Query("SELECT c FROM IpPurchaseOrderEntity c WHERE c.openBy.user = ?1")
    List<IpPurchaseOrderEntity> fetchAllOpenByUsername(String username);

    @Query("SELECT c FROM IpPurchaseOrderEntity c WHERE c.openBy IS NOT NULL")
    List<IpPurchaseOrderEntity> fetchAllOpen();

    @Modifying
    @Query("UPDATE IpPurchaseOrderEntity c SET c.openBy = NULL, c.openAt = NULL WHERE c.id IN (?1)")
    int batchUnlockOpenBy(List<UUID> ids);
}

package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IPurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, UUID>, JpaSpecificationExecutor<PurchaseOrderEntity> {

    @Query("SELECT COUNT(c.id) FROM PurchaseOrderEntity c WHERE c.openBy.id = ?1")
    int countByOpenUserId(UUID userId);

    @Query("SELECT c FROM PurchaseOrderEntity c WHERE c.openBy.user = ?1")
    List<PurchaseOrderEntity> fetchAllOpenByUsername(String username);

    @Query("SELECT c FROM PurchaseOrderEntity c WHERE c.openBy IS NOT NULL")
    List<PurchaseOrderEntity> fetchAllOpen();

    @Modifying
    @Query("UPDATE PurchaseOrderEntity c SET c.openBy = NULL, c.openAt = NULL WHERE c.id IN (?1)")
    int batchUnlockOpenBy(List<UUID> ids);
}

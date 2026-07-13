package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IPurchaseOrderHistoryRepository extends JpaRepository<PurchaseOrderHistoryEntity, UUID> {

    @Query("SELECT h FROM PurchaseOrderHistoryEntity h WHERE h.ipPurchaseOrder = ?1 ORDER BY h.createdAt DESC")
    List<PurchaseOrderHistoryEntity> fetchByPoId(UUID poId);
}

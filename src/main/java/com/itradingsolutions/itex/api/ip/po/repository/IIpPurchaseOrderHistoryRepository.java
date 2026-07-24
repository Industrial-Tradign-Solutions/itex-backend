package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IIpPurchaseOrderHistoryRepository extends JpaRepository<IpPurchaseOrderHistoryEntity, UUID> {

    @Query("SELECT h FROM IpPurchaseOrderHistoryEntity h WHERE h.ipPurchaseOrder = ?1 ORDER BY h.createdAt DESC")
    List<IpPurchaseOrderHistoryEntity> fetchByPoId(UUID poId);
}

package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IPurchaseOrderProductRepository extends JpaRepository<PurchaseOrderProductEntity, UUID> {
}

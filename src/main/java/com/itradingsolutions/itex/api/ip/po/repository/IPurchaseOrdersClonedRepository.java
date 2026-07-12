package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrdersClonedEntity;
import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrdersClonedEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPurchaseOrdersClonedRepository extends JpaRepository<PurchaseOrdersClonedEntity, PurchaseOrdersClonedEntityId> {
}

package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrdersClonedEntity;
import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrdersClonedEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IPurchaseOrdersClonedRepository extends JpaRepository<PurchaseOrdersClonedEntity, PurchaseOrdersClonedEntityId> {

    @Query("SELECT c FROM PurchaseOrdersClonedEntity c WHERE c.id.clonePoId = ?1")
    Optional<PurchaseOrdersClonedEntity> fetchByClonedId(UUID id);
}

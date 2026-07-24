package com.itradingsolutions.itex.api.ip.po.repository;

import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrdersClonedEntity;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrdersClonedEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IIpPurchaseOrdersClonedRepository extends JpaRepository<IpPurchaseOrdersClonedEntity, IpPurchaseOrdersClonedEntityId> {

    @Query("SELECT c FROM IpPurchaseOrdersClonedEntity c WHERE c.id.clonePoId = ?1")
    Optional<IpPurchaseOrdersClonedEntity> fetchByClonedId(UUID id);
}

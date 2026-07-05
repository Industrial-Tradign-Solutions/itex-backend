package com.itradingsolutions.itex.api.ip.q.repository;

import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsClonedEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsClonedEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IIpQuotationClonedRepository extends JpaRepository<IpQuotationsClonedEntity, IpQuotationsClonedEntityId> {

    @Query("SELECT c FROM IpQuotationsClonedEntity c WHERE c.id.cloneQId = ?1")
    Optional<IpQuotationsClonedEntity> fetchByClonedId(UUID id);
}
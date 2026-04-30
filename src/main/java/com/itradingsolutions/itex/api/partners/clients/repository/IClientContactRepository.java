package com.itradingsolutions.itex.api.partners.clients.repository;

import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IClientContactRepository extends JpaRepository<ClientContactEntity, UUID> {
    @Query("SELECT cc FROM ClientContactEntity cc WHERE cc.id = ?1 AND cc.clientInfoDep.client.id = ?2")
    Optional<ClientContactEntity> fetchByClientContactId(UUID clientContactId, UUID clientId);
}

package com.itradingsolutions.itex.api.partners.clients.repository;

import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IClientRepository extends JpaRepository<ClientEntity, UUID>, JpaSpecificationExecutor<ClientEntity> {

    @Deprecated(since = "Solo se usara al principio, mas adelante se elminara")
    @Query("SELECT COUNT(c.id) FROM ClientEntity c WHERE c.status = ?1")
    long countClientsByStatus(ClientStatus status);

    @Query("SELECT c FROM ClientEntity c WHERE c.openBy.user=?1")
    List<ClientEntity> fetchAllOpenByUsername(String username);

    @Query("SELECT c FROM ClientEntity c WHERE c.openBy IS NOT NULL")
    List<ClientEntity> fetchAllOpen();

    @Query("SELECT COUNT(c.id) FROM ClientEntity c WHERE c.openBy.id = ?1")
    int countByOpenUserId(UUID userOpenById);

    @Query("SELECT c FROM ClientEntity c WHERE c.status =?1")
    List<ClientEntity> fetchAllByStatus(ClientStatus status);
}

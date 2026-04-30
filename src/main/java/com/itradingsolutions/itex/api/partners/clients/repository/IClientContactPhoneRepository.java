package com.itradingsolutions.itex.api.partners.clients.repository;

import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactPhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IClientContactPhoneRepository extends JpaRepository<ClientContactPhoneEntity, UUID> {

    @Modifying
    @Query("DELETE FROM ClientContactPhoneEntity p WHERE p.id =?1")
    void deleteByClientContactId(UUID clientId);
}

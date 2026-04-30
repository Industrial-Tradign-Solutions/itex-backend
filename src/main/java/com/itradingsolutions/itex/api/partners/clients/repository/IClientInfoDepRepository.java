package com.itradingsolutions.itex.api.partners.clients.repository;

import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientInfoDepEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IClientInfoDepRepository extends JpaRepository<ClientInfoDepEntity, UUID> {
}

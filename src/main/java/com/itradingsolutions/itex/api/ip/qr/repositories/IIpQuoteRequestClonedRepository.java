package com.itradingsolutions.itex.api.ip.qr.repositories;

import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestsClonedEntity;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestsClonedEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IIpQuoteRequestClonedRepository extends JpaRepository<IpQuoteRequestsClonedEntity, IpQuoteRequestsClonedEntityId> {

    @Query("SELECT qr FROM IpQuoteRequestsClonedEntity qr WHERE qr.id.cloneQrId = ?1")
    Optional<IpQuoteRequestsClonedEntity> fetchByClonedId(UUID id);
}

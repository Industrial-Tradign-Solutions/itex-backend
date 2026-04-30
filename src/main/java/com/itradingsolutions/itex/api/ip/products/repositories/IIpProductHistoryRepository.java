package com.itradingsolutions.itex.api.ip.products.repositories;

import com.itradingsolutions.itex.api.ip.products.models.entity.IpProductHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IIpProductHistoryRepository extends JpaRepository<IpProductHistoryEntity, UUID> {

    @Query("SELECT h FROM IpProductHistoryEntity h WHERE h.product = ?1 ORDER BY h.createdAt DESC")
    List<IpProductHistoryEntity> fetchByProductId(UUID productId);
}

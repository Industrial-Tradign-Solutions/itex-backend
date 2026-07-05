package com.itradingsolutions.itex.api.ip.qr.repositories;

import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface IIpQuoteRequestProductRepository extends JpaRepository<IpQuoteRequestProductEntity, UUID> {
    @Query("""
           SELECT p.id, p.ipProduct.id FROM IpQuoteRequestProductEntity p
           WHERE p.id IN :ids
           """)
    List<Object[]> findProductIdsByIds(@Param("ids") Set<UUID> ids);

    @Query("""
           SELECT p FROM IpQuoteRequestProductEntity p
           WHERE p.id = ?1 AND p.ipQuoteRequest.id = ?2
           """)
    Optional<IpQuoteRequestProductEntity> fetchOneById(UUID id, UUID qrId);

    @Query("""
           SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END
           FROM IpQuoteRequestProductEntity p
           WHERE p.ipProduct.id = ?1 AND p.ipQuoteRequest.id = ?2
           """)
    boolean existsProductById(UUID productId, UUID qrId);

    @Query("""
           SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END
           FROM IpQuoteRequestProductEntity p
           WHERE p.ipProduct.id = ?1 AND p.ipQuoteRequest.id = ?2 AND p.id <> ?3
           """)
    boolean existsProductById(UUID productId, UUID qrId, UUID qrProductId);

    @Modifying
    @Query("""
           DELETE FROM IpQuoteRequestProductEntity p
           WHERE p.ipQuoteRequest.id = ?1 AND p.id = ?2
           """)
    void deleteProductById(UUID qrId, UUID qrProductId);
}

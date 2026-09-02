package com.itradingsolutions.itex.api.ip.products.repositories;

import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductStatusProjection;
import com.itradingsolutions.itex.api.ip.products.models.entity.IpProductEntity;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface IIpProductRepository extends JpaRepository<IpProductEntity, UUID>, JpaSpecificationExecutor<IpProductEntity> {

    @Query("SELECT COUNT(c.id) FROM IpProductEntity c WHERE c.openBy.id = ?1")
    int countByOpenUserId(UUID userOpenById);

    @Query("SELECT c FROM IpProductEntity c WHERE c.openBy.user=?1")
    List<IpProductEntity> fetchAllOpenByUsername(String username);

    @Query("SELECT c FROM IpProductEntity c WHERE c.openBy IS NOT NULL")
    List<IpProductEntity> fetchAllOpen();

    @Query("SELECT c FROM IpProductEntity c WHERE c.status = ?1")
    List<IpProductEntity> fetchAllByStatus(IpProductStatus status);

    @Query("SELECT c FROM IpProductEntity c WHERE c.status IN :status")
    List<IpProductEntity> fetchAllByStatusIn(@Param("status") Collection<IpProductStatus> statuses);

    @Query("SELECT new com.itradingsolutions.itex.api.ip.products.models.dto.IpProductStatusProjection(p.id, p.status) FROM IpProductEntity p WHERE p.id IN :ids")
    List<IpProductStatusProjection> fetchStatusByIds(@Param("ids") Collection<UUID> ids);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM IpProductEntity p WHERE p.description = ?1 AND p.mfrReference = ?2")
    boolean existsProductByDescription(String description, String mfrReference);
}

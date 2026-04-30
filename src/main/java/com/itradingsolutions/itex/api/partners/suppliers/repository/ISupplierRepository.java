package com.itradingsolutions.itex.api.partners.suppliers.repository;

import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.enums.SupplierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ISupplierRepository extends JpaRepository<SupplierEntity, UUID>, JpaSpecificationExecutor<SupplierEntity> {

    @Deprecated(since = "Solo se usara al principio, mas adelante se elminara")
    @Query("SELECT COUNT(s.id) FROM SupplierEntity s WHERE s.status = ?1")
    long countSuppliersByStatus(SupplierStatus status);

    @Query("SELECT s FROM SupplierEntity s WHERE s.openBy.user=?1")
    List<SupplierEntity> fetchAllOpenByUsername(String username);

    @Query("SELECT s FROM SupplierEntity s WHERE s.openBy IS NOT NULL")
    List<SupplierEntity> fetchAllOpen();

    @Query("SELECT s FROM SupplierEntity s WHERE s.status = ?1")
    List<SupplierEntity> fetchAllByStatus(SupplierStatus status);

    @Query("SELECT COUNT(s.id) FROM SupplierEntity s WHERE s.openBy.id = ?1")
    int countByOpenUserId(UUID userOpenById);
}

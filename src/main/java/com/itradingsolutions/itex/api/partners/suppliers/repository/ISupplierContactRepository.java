package com.itradingsolutions.itex.api.partners.suppliers.repository;

import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISupplierContactRepository extends JpaRepository<SupplierContactEntity, UUID> {

    @Query("SELECT cc FROM SupplierContactEntity cc WHERE cc.id = ?1 AND cc.supplierInfoDep.supplier.id = ?2")
    Optional<SupplierContactEntity> fetchBySupplierContactId(UUID supplierContactId, UUID supplierId);
}

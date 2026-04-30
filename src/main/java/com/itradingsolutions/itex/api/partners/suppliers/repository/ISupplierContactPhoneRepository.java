package com.itradingsolutions.itex.api.partners.suppliers.repository;

import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierContactPhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ISupplierContactPhoneRepository extends JpaRepository<SupplierContactPhoneEntity, UUID> {

    @Modifying
    @Query("DELETE FROM SupplierContactPhoneEntity p WHERE p.id =?1")
    void deleteBySupplierContactId(UUID supplierId);
}

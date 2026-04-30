package com.itradingsolutions.itex.api.masters.brand.repositories;

import com.itradingsolutions.itex.api.masters.brand.models.entities.BrandSupplierEntity;
import com.itradingsolutions.itex.api.masters.brand.models.entities.ids.BrandSupplierId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BrandSupplierRepository extends JpaRepository<BrandSupplierEntity, BrandSupplierId> {

    @Modifying
    @Query("DELETE FROM BrandSupplierEntity bs WHERE bs.supplier.id = ?1 AND bs.brand.id = ?2")
    void deleteBySupplierIdAndBrandId(UUID supplierId, UUID brandId);

    @Query("SELECT bs FROM BrandSupplierEntity bs WHERE bs.brand.id = ?1")
    List<BrandSupplierEntity> fetchByBrandId(UUID brandId);
}

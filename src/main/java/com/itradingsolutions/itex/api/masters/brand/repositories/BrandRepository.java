package com.itradingsolutions.itex.api.masters.brand.repositories;

import com.itradingsolutions.itex.api.masters.brand.models.entities.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, UUID>, JpaSpecificationExecutor<BrandEntity> {

    Optional<BrandEntity> findByName(String brandName);
}

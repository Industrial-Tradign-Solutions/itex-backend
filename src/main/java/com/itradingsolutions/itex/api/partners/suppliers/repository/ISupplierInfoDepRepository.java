package com.itradingsolutions.itex.api.partners.suppliers.repository;

import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierInfoDepEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ISupplierInfoDepRepository extends JpaRepository<SupplierInfoDepEntity, UUID> {
}

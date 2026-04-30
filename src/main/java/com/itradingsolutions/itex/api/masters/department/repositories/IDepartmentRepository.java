package com.itradingsolutions.itex.api.masters.department.repositories;

import com.itradingsolutions.itex.api.masters.department.models.entities.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IDepartmentRepository extends JpaRepository<DepartmentEntity, UUID> {

    @Query("SELECT d FROM DepartmentEntity d WHERE d.active = true AND d.clientInfo = true ORDER BY d.name")
    List<DepartmentEntity> fetchAllShowInfoClient();

    @Query("SELECT d FROM DepartmentEntity d WHERE d.active = true AND d.supplierInfo = true ORDER BY d.name")
    List<DepartmentEntity> fetchAllShowInfoSupplier();

    @Query("SELECT d FROM DepartmentEntity d ORDER BY d.name")
    List<DepartmentEntity> fetchAll();
}

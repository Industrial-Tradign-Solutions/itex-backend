package com.itradingsolutions.itex.api.admin.role.repositories;

import com.itradingsolutions.itex.api.admin.role.models.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IRoleRepository extends JpaRepository<RoleEntity, UUID> {

    boolean existsByIdAndActiveIsTrue(UUID roleId);

    @Query("SELECT r FROM RoleEntity r WHERE r.editable = true ORDER BY r.name")
    List<RoleEntity> fetchAll();
    
}

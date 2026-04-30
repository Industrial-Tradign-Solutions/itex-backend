package com.itradingsolutions.itex.api.admin.role.repositories;

import com.itradingsolutions.itex.api.admin.role.models.entities.RoleEntity;
import com.itradingsolutions.itex.api.admin.role.models.entities.RoleMenuEntity;
import com.itradingsolutions.itex.api.admin.role.models.entities.ids.RoleMenuId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IRoleMenuRepository extends JpaRepository<RoleMenuEntity, RoleMenuId> {

    List<RoleMenuEntity> findAllByRole_IdAndMenu_ActiveIsTrue(UUID roleId);
    void deleteAllByRole(RoleEntity roleEntity);
}

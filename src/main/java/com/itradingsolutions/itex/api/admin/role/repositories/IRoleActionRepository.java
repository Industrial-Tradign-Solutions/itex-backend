package com.itradingsolutions.itex.api.admin.role.repositories;

import com.itradingsolutions.itex.api.admin.role.models.entities.RoleActionEntity;
import com.itradingsolutions.itex.api.admin.role.models.entities.RoleEntity;
import com.itradingsolutions.itex.api.admin.role.models.entities.ids.RoleActionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IRoleActionRepository extends JpaRepository<RoleActionEntity, RoleActionId> {

    List<RoleActionEntity> findAllByRole_IdAndAction_ActiveIsTrue(UUID roleId);
    List<RoleActionEntity> findAllByRole_Id(UUID roleId);
    void deleteAllByRole(RoleEntity role);
    void deleteByRole_idAndAction_Id(UUID roleId, long actionId);
}

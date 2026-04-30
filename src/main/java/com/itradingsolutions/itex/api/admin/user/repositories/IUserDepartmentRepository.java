package com.itradingsolutions.itex.api.admin.user.repositories;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserDepartmentEntity;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserDepartmentEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IUserDepartmentRepository extends JpaRepository<UserDepartmentEntity, UserDepartmentEntityId> {

    @Modifying
    @Query("DELETE FROM UserDepartmentEntity p WHERE p.user.id =?1 AND p.department.id = ?2")
    void deleteDepByUserId(UUID userId, UUID departmentId);
}

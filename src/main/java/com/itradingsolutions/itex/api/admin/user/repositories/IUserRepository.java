package com.itradingsolutions.itex.api.admin.user.repositories;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUser(String user);

    @Query("SELECT u FROM UserEntity u WHERE u.role.editable = true ORDER BY u.name")
    List<UserEntity> fetchAll();

    @Query("SELECT u FROM UserEntity u WHERE u.active = true AND u.role.active = true ORDER BY u.name")
    List<UserEntity> fetchAllActive();

    @Query("SELECT u.id FROM UserEntity u WHERE u.active = true AND u.role.id = ?1")
    List<UUID> fetchAllByRoleId(UUID roleId);

    @Query("SELECT u.id FROM UserEntity u WHERE u.active = true")
    List<UUID> fetchAllUserId();
}

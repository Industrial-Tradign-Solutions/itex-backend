package com.itradingsolutions.itex.api.admin.role.repositories;

import com.itradingsolutions.itex.api.admin.role.models.entities.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMenuRepository extends JpaRepository<MenuEntity, Long> {

    @Query("SELECT m FROM MenuEntity m WHERE m.active = true AND m.mainOption = true ORDER BY m.position ASC")
    List<MenuEntity> fetchAll();

    List<MenuEntity> findAllByActiveIsTrueAndMainOptionIsFalse();

    @Query("SELECT m FROM MenuEntity m WHERE m.mainOption = false ORDER BY m.position ASC")
    List<MenuEntity> fetchAllItems();
}

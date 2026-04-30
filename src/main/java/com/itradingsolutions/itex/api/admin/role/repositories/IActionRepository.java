package com.itradingsolutions.itex.api.admin.role.repositories;

import com.itradingsolutions.itex.api.admin.role.models.entities.ActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IActionRepository extends JpaRepository<ActionEntity, Long> {
    List<ActionEntity> findAllByActiveIsTrue();
}

package com.itradingsolutions.itex.api.common.consecutive.repositories;

import com.itradingsolutions.itex.api.common.consecutive.models.entities.ConsecutiveEntity;
import com.itradingsolutions.itex.api.common.consecutive.models.entities.ConsecutiveId;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IConsecutiveRepository extends JpaRepository<ConsecutiveEntity, ConsecutiveId> {

    @Query("SELECT c FROM ConsecutiveEntity c WHERE c.id.clientCode = ?1 AND c.id.year = ?2 AND c.id.month = ?3 AND c.id.module = ?4 AND c.id.department = ?5 ORDER BY c.id.number")
    List<ConsecutiveEntity> fetchByPrefix(String clientCode, String year, String month, ConsecutiveModule module, ConsecutiveDepartment department);

    @Query("SELECT MAX(c.id.number) FROM ConsecutiveEntity c WHERE c.id.clientCode = ?1 AND c.id.year = ?2 AND c.id.month = ?3 AND c.id.module = ?4 AND c.id.department = ?5")
    Optional<Integer> fetchMaxByPrefix(String clientCode, String year, String month, ConsecutiveModule module, ConsecutiveDepartment department);
}

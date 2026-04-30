package com.itradingsolutions.itex.api.masters.location.repositories;

import com.itradingsolutions.itex.api.masters.location.models.entities.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IStateRepository extends JpaRepository<StateEntity, UUID> {

    @Query("SELECT c FROM StateEntity c ORDER BY c.name")
    List<StateEntity> fetchAll();
}

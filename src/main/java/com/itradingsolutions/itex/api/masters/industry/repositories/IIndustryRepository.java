package com.itradingsolutions.itex.api.masters.industry.repositories;

import com.itradingsolutions.itex.api.masters.industry.models.entities.IndustryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IIndustryRepository extends JpaRepository<IndustryEntity, UUID> {

    @Query("SELECT d FROM IndustryEntity d ORDER BY d.name")
    List<IndustryEntity> fetchAll();
}

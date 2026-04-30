package com.itradingsolutions.itex.api.masters.location.repositories;

import com.itradingsolutions.itex.api.masters.location.models.entities.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICountryRepository  extends JpaRepository<CountryEntity, UUID> {

    @Query("SELECT c FROM CountryEntity c ORDER BY c.name")
    List<CountryEntity> fetchAll();


    @Query("SELECT c FROM CountryEntity c WHERE c.name = ?1 OR c.nameShort = ?1")
    Optional<CountryEntity> fetchOneByNameOrNameShort(String name);
}

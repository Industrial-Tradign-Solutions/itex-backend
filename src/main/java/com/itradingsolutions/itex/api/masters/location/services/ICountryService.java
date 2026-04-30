package com.itradingsolutions.itex.api.masters.location.services;


import com.itradingsolutions.itex.api.common.service.CommonService;
import com.itradingsolutions.itex.api.masters.location.models.dto.CountryDTO;
import com.itradingsolutions.itex.api.masters.location.models.entities.CountryEntity;

import java.util.Optional;
import java.util.UUID;

public interface ICountryService extends CommonService<CountryDTO> {

    CountryEntity findEntityById(UUID countryId);
    Optional<CountryDTO> findByNameOrNameShort(String name);
}

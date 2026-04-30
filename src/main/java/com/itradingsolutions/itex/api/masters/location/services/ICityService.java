package com.itradingsolutions.itex.api.masters.location.services;


import com.itradingsolutions.itex.api.common.service.CommonService;
import com.itradingsolutions.itex.api.masters.location.models.dto.CityDTO;
import com.itradingsolutions.itex.api.masters.location.models.entities.CityEntity;

import java.util.UUID;

public interface ICityService extends CommonService<CityDTO> {

    CityEntity findEntityById(UUID id);

}

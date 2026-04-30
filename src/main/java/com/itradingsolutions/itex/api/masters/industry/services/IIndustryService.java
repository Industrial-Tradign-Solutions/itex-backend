package com.itradingsolutions.itex.api.masters.industry.services;

import com.itradingsolutions.itex.api.common.service.CommonService;
import com.itradingsolutions.itex.api.masters.industry.models.dto.IndustryDTO;
import com.itradingsolutions.itex.api.masters.industry.models.entities.IndustryEntity;

import java.util.UUID;

public interface IIndustryService extends CommonService<IndustryDTO> {

    IndustryEntity findEntityById(UUID id, boolean validateActive);

}

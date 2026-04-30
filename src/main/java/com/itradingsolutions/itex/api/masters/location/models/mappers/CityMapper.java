package com.itradingsolutions.itex.api.masters.location.models.mappers;

import com.itradingsolutions.itex.api.masters.common.models.mappers.CommonMasterMapper;
import com.itradingsolutions.itex.api.masters.location.models.dto.CityDTO;
import com.itradingsolutions.itex.api.masters.location.models.entities.CityEntity;
import com.itradingsolutions.itex.api.masters.location.models.requests.CityRequest;
import com.itradingsolutions.itex.api.masters.location.models.responses.BasicCityResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.CityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CityMapper extends CommonMasterMapper<CityDTO, BasicCityResponse, CityResponse, CityRequest> {

    CityDTO entityToDto(CityEntity entity);
}

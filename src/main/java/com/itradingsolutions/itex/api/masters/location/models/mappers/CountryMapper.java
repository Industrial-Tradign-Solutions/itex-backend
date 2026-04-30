package com.itradingsolutions.itex.api.masters.location.models.mappers;

import com.itradingsolutions.itex.api.masters.common.models.mappers.CommonMasterMapper;
import com.itradingsolutions.itex.api.masters.location.models.dto.CountryDTO;
import com.itradingsolutions.itex.api.masters.location.models.entities.CountryEntity;
import com.itradingsolutions.itex.api.masters.location.models.requests.CountryRequest;
import com.itradingsolutions.itex.api.masters.location.models.responses.BasicCountryResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.CountryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CountryMapper extends CommonMasterMapper<CountryDTO, BasicCountryResponse, CountryResponse, CountryRequest> {

    CountryDTO entityToDto(CountryEntity entity);

}

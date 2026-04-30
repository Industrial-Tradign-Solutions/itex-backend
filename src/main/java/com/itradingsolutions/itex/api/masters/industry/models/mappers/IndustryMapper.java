package com.itradingsolutions.itex.api.masters.industry.models.mappers;

import com.itradingsolutions.itex.api.masters.common.models.mappers.CommonMasterMapper;
import com.itradingsolutions.itex.api.masters.industry.models.dto.IndustryDTO;
import com.itradingsolutions.itex.api.masters.industry.models.entities.IndustryEntity;
import com.itradingsolutions.itex.api.masters.industry.models.requests.IndustryRequest;
import com.itradingsolutions.itex.api.masters.industry.models.responses.BasicIndustryResponse;
import com.itradingsolutions.itex.api.masters.industry.models.responses.IndustryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IndustryMapper extends CommonMasterMapper<IndustryDTO, BasicIndustryResponse, IndustryResponse, IndustryRequest> {

    IndustryDTO entityToDto(IndustryEntity entity);

}

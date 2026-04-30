package com.itradingsolutions.itex.api.masters.location.models.mappers;

import com.itradingsolutions.itex.api.masters.common.models.mappers.CommonMasterMapper;
import com.itradingsolutions.itex.api.masters.location.models.dto.StateDTO;
import com.itradingsolutions.itex.api.masters.location.models.entities.StateEntity;
import com.itradingsolutions.itex.api.masters.location.models.requests.StateRequest;
import com.itradingsolutions.itex.api.masters.location.models.responses.BasicStateResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.StateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StateMapper extends CommonMasterMapper<StateDTO, BasicStateResponse, StateResponse, StateRequest> {

    StateDTO entityToDto(StateEntity entity);

}

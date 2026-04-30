package com.itradingsolutions.itex.api.masters.location.services;

import com.itradingsolutions.itex.api.common.service.CommonService;
import com.itradingsolutions.itex.api.masters.location.models.dto.StateDTO;
import com.itradingsolutions.itex.api.masters.location.models.entities.StateEntity;

import java.util.UUID;

public interface IStateService extends CommonService<StateDTO> {

    StateEntity findEntityById(UUID stateId);

}

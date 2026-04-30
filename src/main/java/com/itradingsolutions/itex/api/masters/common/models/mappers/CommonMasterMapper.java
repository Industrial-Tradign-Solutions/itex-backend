package com.itradingsolutions.itex.api.masters.common.models.mappers;

import com.itradingsolutions.itex.api.masters.common.models.dto.BaseMasterDTO;
import com.itradingsolutions.itex.api.masters.common.models.requests.BaseMasterRequest;
import com.itradingsolutions.itex.api.masters.common.models.responses.BaseBasicMasterResponse;

public interface CommonMasterMapper<
        D extends BaseMasterDTO,
        B extends BaseBasicMasterResponse,
        R extends BaseBasicMasterResponse,
        Q extends BaseMasterRequest> {

    B dtoToResponseBasic(D dto);
    R dtoToResponse(D dto);
    D requestToDTO(Q request);
}

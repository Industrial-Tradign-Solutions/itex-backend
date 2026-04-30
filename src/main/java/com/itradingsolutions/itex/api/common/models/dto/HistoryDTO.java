package com.itradingsolutions.itex.api.common.models.dto;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public abstract class HistoryDTO extends BaseDTO {

    private UserDTO user;
    private Map<String, Object> data;
}

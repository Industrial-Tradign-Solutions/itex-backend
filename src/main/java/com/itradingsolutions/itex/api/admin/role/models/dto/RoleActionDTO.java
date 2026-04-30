package com.itradingsolutions.itex.api.admin.role.models.dto;


import com.itradingsolutions.itex.api.admin.role.models.dto.ids.RoleActionIdDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class RoleActionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8934250172111551659L;

    private RoleActionIdDTO id;
    private ActionDTO action;

}

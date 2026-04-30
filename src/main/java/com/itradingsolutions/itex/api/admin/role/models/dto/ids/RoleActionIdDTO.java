package com.itradingsolutions.itex.api.admin.role.models.dto.ids;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
public class RoleActionIdDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7361946816315572863L;
    private UUID roleId;
    private Long actionId;
}

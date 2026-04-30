package com.itradingsolutions.itex.api.admin.role.models.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenuRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5811784545302468439L;
    private List<Long> menuIds;
}

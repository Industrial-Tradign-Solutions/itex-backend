package com.itradingsolutions.itex.api.admin.role.models.entities.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class RoleMenuId implements Serializable {


    @Serial
    private static final long serialVersionUID = 1532619667005623253L;

    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "menu_id")
    private Long menuId;
}

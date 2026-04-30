package com.itradingsolutions.itex.api.admin.role.models.entities;

import com.itradingsolutions.itex.api.admin.role.models.entities.ids.RoleMenuId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "t_roles_menus")
@Getter
@Setter
public class RoleMenuEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -5920370725438178407L;

    @EmbeddedId
    private RoleMenuId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("menuId")
    @JoinColumn(name = "menu_id", nullable = false)
    private MenuEntity menu;
}

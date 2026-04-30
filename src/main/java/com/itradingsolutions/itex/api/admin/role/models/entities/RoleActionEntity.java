package com.itradingsolutions.itex.api.admin.role.models.entities;

import com.itradingsolutions.itex.api.admin.role.models.entities.ids.RoleActionId;
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
@Table(name = "t_roles_actions")
@Getter
@Setter
public class RoleActionEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1422939953797583184L;

    @EmbeddedId
    private RoleActionId id;


    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("actionId")
    @JoinColumn(name = "action_id", nullable = false)
    private ActionEntity action;
}

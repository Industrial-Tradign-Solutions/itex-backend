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
public class RoleActionId implements Serializable {

    @Serial
    private static final long serialVersionUID = -5942127278984216513L;

    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "action_id")
    private Long actionId;

}

package com.itradingsolutions.itex.api.admin.role.models.entities;

import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.masters.common.models.entities.BaseMasterWithDescriptionEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.util.List;

@Entity
@Table(name = "t_roles", uniqueConstraints = @UniqueConstraint(columnNames = "name", name = UniqueDB.ROLE_NAME))
@Getter
@Setter
@ToString
public class RoleEntity extends BaseMasterWithDescriptionEntity {

    @Serial
    private static final long serialVersionUID = 8789199408393001825L;

    @Column(name = "is_editable", nullable = false)
    private boolean editable = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
    @ToString.Exclude
    private List<RoleMenuEntity> menus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "role")
    @ToString.Exclude
    private List<RoleActionEntity> actions;
}

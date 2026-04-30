package com.itradingsolutions.itex.api.admin.user.models.entities;

import com.itradingsolutions.itex.api.admin.role.services.IRoleService;
import com.itradingsolutions.itex.api.admin.role.models.entities.RoleEntity;
import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "t_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username", name = UniqueDB.USER_USERNAME)
})
@Getter
@Setter
@ToString
public class UserEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2518374272402702544L;

    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @Column(name = "last_name", length = 60, nullable = false)
    private String lastName;

    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "username", length = 50, nullable = false)
    private String user;

    @Column(name = "password", length = 500, nullable = false)
    private String pass;

    @Column(name = "email_password", length = 500)
    private String emailPassword;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "is_pass_changed", nullable = false)
    private boolean passChanged;

    @Column(name = "pass_changed_at")
    private ZonedDateTime passChangedAt;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade =  CascadeType.ALL)
    @ToString.Exclude
    private List<UserDepartmentEntity> departments;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "extension", length = 5, nullable = false)
    private String extension;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    public String getFullName(){
        return this.name + " " + this.lastName;
    }

    public boolean isActive() {
        if (this.role.getId().equals(IRoleService.SUPER_ADMIN_ID))
            return this.active;
        return this.active && this.role.isActive();
    }
}

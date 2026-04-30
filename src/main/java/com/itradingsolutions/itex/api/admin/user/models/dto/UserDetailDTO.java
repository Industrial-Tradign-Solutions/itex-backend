package com.itradingsolutions.itex.api.admin.user.models.dto;

import com.itradingsolutions.itex.api.admin.role.models.dto.RoleDTO;
import com.itradingsolutions.itex.api.masters.department.models.dto.DepartmentDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class UserDetailDTO implements Serializable, UserDetails {

    @Serial
    private static final long serialVersionUID = 3342798170297633556L;

    private UUID id;
    private String name;
    private String lastName;
    private String email;
    private String pass;
    private boolean active;
    private RoleDTO role;
    private boolean passChanged;
    private String user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.getRole().getName()));
    }
    @Override
    public String getPassword() {
        return this.getPass();
    }

    @Override
    public String getUsername() {
        return this.getUser();
    }

    @Override
    public boolean isEnabled() {
        return this.isActive();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}

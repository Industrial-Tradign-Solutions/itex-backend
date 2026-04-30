package com.itradingsolutions.itex.api.admin.user.models.dto;

import com.itradingsolutions.itex.api.admin.role.models.dto.RoleDTO;
import com.itradingsolutions.itex.api.masters.department.models.dto.DepartmentDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7464744834129055276L;

    private UUID id;
    private RoleDTO role;
    private List<UserDepartmentDTO> departments;
    private String name;
    private String fullName;
    private String lastName;
    private String email;
    private String emailPassword;
    private Boolean active;
    private Boolean passChanged;
    private ZonedDateTime passChangedAt;
    private ZonedDateTime createdAt;
    private String title;
    private String extension;
    private String user;
    private String photoUrl;

    public List<DepartmentDTO> getDepartments() {
        return this.departments.stream().map(UserDepartmentDTO::getDepartment).toList();
    }
}

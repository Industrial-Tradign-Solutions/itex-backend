package com.itradingsolutions.itex.api.admin.user.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class UserDepartmentEntityId implements Serializable {
    @Serial
    private static final long serialVersionUID = -6751777402757667445L;


    @Column(name = "id_user", nullable = false)
    private UUID idUser;


    @Column(name = "id_department", nullable = false)
    private UUID idDepartment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserDepartmentEntityId entity = (UserDepartmentEntityId) o;
        return Objects.equals(this.idUser, entity.idUser) &&
                Objects.equals(this.idDepartment, entity.idDepartment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, idDepartment);
    }

}
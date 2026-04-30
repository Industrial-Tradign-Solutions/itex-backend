package com.itradingsolutions.itex.api.masters.department.models.entities;

import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.masters.common.models.entities.BaseMasterWithDescriptionEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Entity
@Table(name = "t_departments", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name", name = UniqueDB.DEPARTMENT_NAME)
})
@Getter
@Setter
@ToString
public class DepartmentEntity extends BaseMasterWithDescriptionEntity {

    @Serial
    private static final long serialVersionUID = 1137949518854128307L;

    @Column(name = "is_client_info", nullable = false)
    private boolean clientInfo;

    @Column(name = "is_supplier_info", nullable = false)
    private boolean supplierInfo;
}

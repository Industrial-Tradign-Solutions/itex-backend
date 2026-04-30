package com.itradingsolutions.itex.api.partners.common.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.masters.department.models.entities.DepartmentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class PartnerInfoByDepEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -4302622186921290238L;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentEntity department;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

}

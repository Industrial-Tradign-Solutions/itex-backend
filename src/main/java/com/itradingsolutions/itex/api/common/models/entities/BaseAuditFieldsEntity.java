package com.itradingsolutions.itex.api.common.models.entities;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class BaseAuditFieldsEntity extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 6268358199503058690L;

    @ManyToOne
    @JoinColumn(name = "open_by_user_id")
    private UserEntity openBy;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private UserEntity createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id")
    private UserEntity updatedBy;

    @Column(name = "open_at")
    private ZonedDateTime openAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;


}

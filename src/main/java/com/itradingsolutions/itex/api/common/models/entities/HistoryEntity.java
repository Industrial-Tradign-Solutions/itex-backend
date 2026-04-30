package com.itradingsolutions.itex.api.common.models.entities;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.util.Map;

@Getter
@Setter
@MappedSuperclass
public abstract class HistoryEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 7948614754348583383L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "data")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> data;
}

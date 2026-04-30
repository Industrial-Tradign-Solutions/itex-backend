package com.itradingsolutions.itex.api.common.util.models.entities;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.HistoryActions;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "itex_history")
public class HistoryEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -7329186373149372974L;

    @Column(name = "module_id", nullable = false)
    private Long module;

    @Column(name = "action", nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private HistoryActions action;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_executed_action_id", nullable = false)
    private UserEntity userExecutedAction;

    @Column(name = "modified_record_id")
    private UUID modifiedRecord;

    @Column(name = "old_data")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> oldData;

    @Column(name = "new_data")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> newData;

    @Column(name = "is_basic", nullable = false)
    private boolean basic;
}

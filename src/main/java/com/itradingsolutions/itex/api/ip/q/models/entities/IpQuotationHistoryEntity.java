package com.itradingsolutions.itex.api.ip.q.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.HistoryEntity;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationHistoryAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.util.UUID;

/**
 * Entity for tracking history of changes in IP Quotations.
 * Maps to table t_ip_quotation_history.
 */
@Entity
@Table(name = "t_ip_quotation_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpQuotationHistoryEntity extends HistoryEntity {

    @Serial
    private static final long serialVersionUID = 9087654321234567890L;

    @Column(name = "ip_q_id", nullable = false)
    private UUID ipQuotation;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private IpQuotationHistoryAction action;
}

package com.itradingsolutions.itex.api.ip.qr.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.HistoryEntity;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestHistoryAction;
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

@Entity
@Table(name = "t_ip_quote_request_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpQuoteRequestHistoryEntity extends HistoryEntity {

    @Serial
    private static final long serialVersionUID = 1725639594644956156L;
    @Column(name = "ip_qr_id", nullable = false)
    private UUID ipQuoteRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 100)
    private IpQuoteRequestHistoryAction action;

}

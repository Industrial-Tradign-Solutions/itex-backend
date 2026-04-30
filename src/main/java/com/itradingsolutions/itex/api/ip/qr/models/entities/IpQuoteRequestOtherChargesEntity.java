package com.itradingsolutions.itex.api.ip.qr.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "t_ip_quote_requests_other_charges", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ip_qr_id", "description"}, name = UniqueDB.IP_QR_OTHER_CHARGES_UNIQUE)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpQuoteRequestOtherChargesEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -1016081317000164395L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_qr_id", nullable = false)
    private IpQuoteRequestEntity ipQuoteRequest;

    @Column(name = "value", nullable = false, precision = 15, scale = 5)
    private BigDecimal value = BigDecimal.ZERO;

    @Column(name = "description", nullable = false, length = 150)
    private String description;
}

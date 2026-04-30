package com.itradingsolutions.itex.api.ip.qr.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.common.util.models.enums.UnitType;
import com.itradingsolutions.itex.api.ip.products.models.entity.IpProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "t_ip_quote_request_products", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ip_qr_id", "ip_product_id"}, name = UniqueDB.IP_QR_PRODUCT_UNIQUE)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpQuoteRequestProductEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2562147161350971996L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_qr_id", nullable = false)
    private IpQuoteRequestEntity ipQuoteRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_product_id", nullable = false)
    private IpProductEntity ipProduct;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "quantity", nullable = false, precision = 15, scale = 5)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type", nullable = false)
    private UnitType unitType;

    @Column(name = "lead_time")
    private Integer leadTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "lead_time_type")
    private LeadTime leadTimeType;

    @Column(name = "unit_price", precision = 15, scale = 5)
    private BigDecimal unitPrice;
}

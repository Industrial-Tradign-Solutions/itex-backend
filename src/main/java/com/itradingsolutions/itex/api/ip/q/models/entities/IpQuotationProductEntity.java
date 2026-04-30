package com.itradingsolutions.itex.api.ip.q.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationProductCondition;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "t_ip_quotation_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpQuotationProductEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 4785147161350121006L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotations_quote_request_id", nullable = false)
    private IpQuotationsQuoteRequestEntity quotationsQuoteRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_request_product_id")
    private IpQuoteRequestProductEntity quoteRequestProduct;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "profit_margin", nullable = false, precision = 5, scale = 4)
    private BigDecimal profitMargin = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition", nullable = false, length = 20)
    private IpQuotationProductCondition condition;
}

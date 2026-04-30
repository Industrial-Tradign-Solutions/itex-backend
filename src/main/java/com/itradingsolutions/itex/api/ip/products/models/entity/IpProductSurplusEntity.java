package com.itradingsolutions.itex.api.ip.products.models.entity;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductSurplusType;
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

import java.io.Serial;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "t_ip_products_surplus")
public class IpProductSurplusEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -9158921586426857761L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private IpProductEntity product;

    @Column(name = "quantity", nullable = false, scale = 3, precision = 15)
    private BigDecimal quantity;

    @Column(name = "price", nullable = false, scale = 3, precision = 15)
    private BigDecimal price;

    @Column(name = "wh_number", nullable = false, length = 100)
    private String whNumber;

    @Column(name = "location", nullable = false, length = 100)
    private String location;

    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private IpProductSurplusType type;
}

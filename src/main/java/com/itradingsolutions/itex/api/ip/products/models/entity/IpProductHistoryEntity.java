package com.itradingsolutions.itex.api.ip.products.models.entity;

import com.itradingsolutions.itex.api.common.models.entities.HistoryEntity;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductHistoryActions;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.UUID;

@Entity
@Table(name = "t_ip_products_history")
@Getter
@Setter
public class IpProductHistoryEntity extends HistoryEntity {

    @Serial
    private static final long serialVersionUID = 7909710246192362160L;

    @Column(name = "product_id", nullable = false)
    private UUID product;

    @Column(name = "action", nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private IpProductHistoryActions action;
}

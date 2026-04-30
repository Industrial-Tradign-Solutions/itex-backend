package com.itradingsolutions.itex.api.ip.products.models.entity;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import com.itradingsolutions.itex.api.masters.brand.models.entities.BrandEntity;
import com.itradingsolutions.itex.api.masters.location.models.entities.CountryEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "t_ip_products")
public class IpProductEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -670931777989750064L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private BrandEntity brand;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "client_description", length = 1000)
    private String clientDescription;

    @Column(name = "mfr_reference", length = 100)
    private String mfrReference;

    @Column(name = "client_reference", length = 100)
    private String clientReference;

    @Column(name = "net_weight_lbs", scale = 3, precision = 15)
    private BigDecimal netWeightLbs;

    @Column(name = "nmfc")
    private Integer nmfc;

    @Column(name = "freight_class", length = 150)
    @Enumerated(EnumType.STRING)
    private FreightClass freightClass;

    @Column(name = "status", nullable = false, length = 25)
    @Enumerated(EnumType.STRING)
    private IpProductStatus status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords;

    @Column(name = "hts_schedule_b_number")
    private Integer htsScheduleBNumber;

    @Column(name = "eccn", length = 100)
    private String eccn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coo")
    private CountryEntity coo;

    @Column(name = "is_battery", nullable = false)
    private boolean battery;

    @Column(name = "is_hazmat", nullable = false)
    private boolean hazmat;

    @Column(name = "dual_use", nullable = false)
    private boolean dualUse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "substitute_product_id")
    private IpProductEntity substituteProduct;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "product", cascade =  CascadeType.ALL)
    private List<IpProductSurplusEntity> surplus;

    @Column(name = "open_at")
    private ZonedDateTime openAt;

    @ManyToOne
    @JoinColumn(name = "open_by_user_id")
    private UserEntity openBy;
}

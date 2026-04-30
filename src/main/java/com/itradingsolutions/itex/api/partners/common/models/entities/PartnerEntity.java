package com.itradingsolutions.itex.api.partners.common.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseAuditFieldsEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.Language;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentMethod;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.masters.location.models.entities.CityEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class PartnerEntity extends BaseAuditFieldsEntity {

    @Serial
    private static final long serialVersionUID = 7232967155408625174L;

    @Column(name = "name", nullable = false, length = 300)
    private String name;

    @Column(name = "tax_id", unique = true, length = 35)
    private String taxId;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, length = 30)
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 40)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_terms", length = 40)
    private PaymentTerms paymentTerms;

    @Column(name = "address", length = 500)
    private String address;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private CityEntity city;

    @Column(name = "zip_code", length = 30)
    private String zipCode;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "web_site", length = 200)
    private String webSite;

    @Column(name = "phone_country_code", length = 3)
    private String countryCode;

    @Column(name = "phone_city_code", length = 3)
    private String cityCode;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

}

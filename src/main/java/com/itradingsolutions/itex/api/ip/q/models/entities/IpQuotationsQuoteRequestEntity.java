package com.itradingsolutions.itex.api.ip.q.models.entities;

import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(
        name = "t_ip_quotations_quote_request",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"quotation_id", "quote_request_id"}, name = UniqueDB.IP_Q_QR_UNIQUE)
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IpQuotationsQuoteRequestEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1877995719608898660L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id")
    private IpQuotationEntity quotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_request_id")
    private IpQuoteRequestEntity quoteRequest;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "quotationsQuoteRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IpQuotationProductEntity> quotationProducts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "quotationsQuoteRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IpQuotationOtherChargesQuoteRequestEntity> quotationsOtherCharges;

    public int getMaxNumberOfProducts() {
        if (quotationProducts == null || quotationProducts.isEmpty()) return 1;
        return quotationProducts.stream()
                .mapToInt(IpQuotationProductEntity::getNumber)
                .max()
                .orElse(0) + 1;
    }
}
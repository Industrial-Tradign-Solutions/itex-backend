package com.itradingsolutions.itex.api.ip.qr.models.entities;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactEntity;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierContactEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Entity
@Table(
        name = "t_ip_quote_requests",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"number"}, name = UniqueDB.IP_QR_NUMBER)
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IpQuoteRequestEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 4017604577724210437L;

    @Column(name = "number", nullable = false, length = 20, unique = true)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private IpQuoteRequestStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", length = 20, nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_contact_id")
    private ClientContactEntity clientContact;

    @Column(name = "client_qr_number", length = 50)
    private String clientQrNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_rep_id", nullable = false)
    private UserEntity salesRep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private SupplierEntity supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_contact_id")
    private SupplierContactEntity supplierContact;

    @Column(name = "supplier_qr_number", length = 50)
    private String supplierQrNumber;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "internal_remarks", columnDefinition = "TEXT")
    private String internalRemarks;

    @Column(name = "shipping_point_zip_code", length = 50)
    private String shippingPointZipCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "freight_class", length = 50)
    private FreightClass freightClass;

    @Column(name = "fob_shipping_point", length = 150)
    private String fobShippingPoint;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_terms", length = 40)
    private PaymentTerms paymentTerms;

    @Column(name = "freight_charges", scale = 2, precision = 15)
    private BigDecimal freightCharges;

    @Column(name = "open_at")
    private ZonedDateTime openAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_by_user_id")
    private UserEntity openBy;

    @Column(name = "path_pdf", length = 1000)
    private String pdfUrl;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ipQuoteRequest", cascade = CascadeType.ALL)
    private List<IpQuoteRequestProductEntity> products;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ipQuoteRequest", cascade = CascadeType.ALL)
    private List<IpQuoteRequestOtherChargesEntity> otherCharges;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mainQr", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IpQuoteRequestsClonedEntity> clonedQrs;
    
    @Column(name = "sent_at")
    private ZonedDateTime sentAt;

    @Column(name = "answered_at")
    private ZonedDateTime answeredAt;

    @Column(name = "complete_at")
    private ZonedDateTime completeAt;

    @Column(name = "reject_at")
    private ZonedDateTime rejectAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "quoteRequest", cascade = CascadeType.ALL)
    private List<IpQuotationsQuoteRequestEntity> quotationsQuoteRequests;

    public List<IpQuoteRequestProductEntity> getProducts() {
        return Optional.ofNullable(products)
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream()
                        .sorted(Comparator.comparingInt(IpQuoteRequestProductEntity::getNumber))
                        .toList())
                .orElse(Collections.emptyList());
    }

    public List<IpQuoteRequestEntity> getClonedQrs() {
        return Optional.ofNullable(clonedQrs)
                .orElseGet(List::of)
                .stream()
                .map(IpQuoteRequestsClonedEntity::getClonedQr)
                .toList();
    }

    public int getMaxNumberOfProducts() {
        return Optional.ofNullable(products)
            .filter(list -> !list.isEmpty())
            .map(list -> list.stream()
                .mapToInt(IpQuoteRequestProductEntity::getNumber)
                .max()
                .orElse(0))
            .orElse(0) + 1;
    }

    public boolean isValidAnswered() {
        return Optional.ofNullable(this.products)
            .filter(p -> !p.isEmpty())
            .map(p -> p.stream().allMatch(product ->
                product.getUnitPrice() != null &&
                product.getLeadTime() != null &&
                product.getLeadTimeType() != null))
            .orElse(false);
    }
}

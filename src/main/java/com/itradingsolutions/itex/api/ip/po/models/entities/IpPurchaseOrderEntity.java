package com.itradingsolutions.itex.api.ip.po.models.entities;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderStatus;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.masters.location.models.entities.CityEntity;
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
import java.util.List;

@Entity
@Table(name = "t_ip_purchase_orders", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"number"}, name = UniqueDB.IP_PO_NUMBER)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IpPurchaseOrderEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1007449746510820200L;

    @Column(name = "number", nullable = false, length = 20, unique = true)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private IpPurchaseOrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", length = 20, nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id")
    private IpQuotationEntity quotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_contact_id")
    private ClientContactEntity clientContact;

    @Column(name = "client_po_number", length = 50)
    private String clientPoNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private SupplierEntity supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_contact_id")
    private SupplierContactEntity supplierContact;

    @Column(name = "supplier_po_number", length = 50)
    private String supplierPoNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_rep_id")
    private UserEntity salesRep;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_terms", length = 40)
    private PaymentTerms paymentTerms;

    @Column(name = "lead_time", nullable = false)
    private Integer leadTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "lead_time_type", nullable = false, length = 20)
    private LeadTime leadTimeType;

    @Column(name = "shipping_method", length = 50)
    private String shippingMethod;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "internal_remarks", columnDefinition = "TEXT")
    private String internalRemarks;

    @Column(name = "ship_to_name", nullable = false, length = 300)
    private String shipToName;

    @Column(name = "ship_to_address", nullable = false, length = 500)
    private String shipToAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_to_city", nullable = false)
    private CityEntity shipToCity;

    @Column(name = "ship_to_phone", nullable = false, length = 20)
    private String shipToPhone;

    @Column(name = "ship_to_contact_name", nullable = false, length = 50)
    private String shipToContactName;

    @Column(name = "ship_to_email", nullable = false, length = 100)
    private String shipToEmail;

    @Column(name = "sales_tax", nullable = false, precision = 15, scale = 2)
    private BigDecimal salesTax;

    @Column(name = "path_pdf", length = 1000)
    private String pdfUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_by_user_id")
    private UserEntity openBy;

    @Column(name = "open_at")
    private ZonedDateTime openAt;

    @Column(name = "sent_at")
    private ZonedDateTime sentAt;

    @Column(name = "answered_at")
    private ZonedDateTime answeredAt;

    @Column(name = "complete_at")
    private ZonedDateTime completeAt;

    @Column(name = "reject_at")
    private ZonedDateTime rejectAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IpPurchaseOrderProductEntity> products;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IpPurchaseOrderOtherChargeEntity> otherCharges;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IpPurchaseOrderOtherChargesQuotationEntity> importedQuotationCharges;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IpPurchaseOrderOtherChargesQuotationQrEntity> importedQuoteRequestCharges;
}

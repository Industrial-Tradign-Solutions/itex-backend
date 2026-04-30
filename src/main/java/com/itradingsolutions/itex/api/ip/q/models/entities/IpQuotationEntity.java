package com.itradingsolutions.itex.api.ip.q.models.entities;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.Incoterms;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactEntity;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
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
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(
        name = "t_ip_quotations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"number"}, name = UniqueDB.IP_Q_NUMBER)
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IpQuotationEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1007449746510820104L;

    @Column(name = "number", nullable = false, length = 20, unique = true)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private IpQuotationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", length = 20, nullable = false)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_contact_id")
    private ClientContactEntity clientContact;

    @Column(name = "client_q_number", length = 50)
    private String clientQNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sales_rep_id", nullable = false)
    private UserEntity salesRep;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "internal_remarks", columnDefinition = "TEXT")
    private String internalRemarks;

    @Column(name = "lead_time", nullable = false)
    private Integer leadTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "lead_time_type", nullable = false, length = 20)
    private LeadTime leadTimeType;

    @Column(name = "validity", nullable = false)
    private Integer validity;

    @Enumerated(EnumType.STRING)
    @Column(name = "validity_type", nullable = false, length = 20)
    private LeadTime validityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "incoterms", length = 3)
    private Incoterms incoterms;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_terms", length = 40, nullable = false)
    private PaymentTerms paymentTerms;

    @Column(name = "application_at", nullable = false)
    private ZonedDateTime applicationAt;

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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "quotation", cascade = CascadeType.ALL)
    private List<IpQuotationsQuoteRequestEntity> quoteRequestsQuotations;
}
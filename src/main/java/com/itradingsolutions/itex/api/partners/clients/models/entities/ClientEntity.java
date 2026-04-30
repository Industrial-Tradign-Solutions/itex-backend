package com.itradingsolutions.itex.api.partners.clients.models.entities;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.masters.industry.models.entities.IndustryEntity;
import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;

import com.itradingsolutions.itex.api.partners.common.models.entities.PartnerEntity;
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
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "t_clients", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"}, name = UniqueDB.CLIENT_CODE),
        @UniqueConstraint(columnNames = {"tax_id"}, name = UniqueDB.CLIENT_TAX_ID)
})
@Getter
@Setter
public class ClientEntity extends PartnerEntity {

    @Serial
    private static final long serialVersionUID = -5505707155225122112L;

    @Column(name = "code", nullable = false, length = 3)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 25)
    private ClientStatus status;

    @ManyToOne
    @JoinColumn(name = "industry_id")
    private IndustryEntity industry;

    @ManyToOne
    @JoinColumn(name = "change_prospect_to_client_by_user_id")
    private UserEntity changeProspectToClientBy;

    @Column(name = "change_prospect_to_client_at")
    private ZonedDateTime changeProspectToClientAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "client", cascade =  CascadeType.ALL)
    private List<ClientInfoDepEntity> infoByDepartment;
}

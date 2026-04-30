package com.itradingsolutions.itex.api.partners.clients.models.entities;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.partners.common.models.entities.PartnerInfoByDepEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "t_clients_info_by_department", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"client_id", "department_id"}, name = UniqueDB.CLIENT_DEPARTMENT_INFO),
})
@Getter
@Setter
public class ClientInfoDepEntity extends PartnerInfoByDepEntity {

    @Serial
    private static final long serialVersionUID = 1330685926417694691L;

    @ManyToOne
    @JoinColumn(name = "account_rep")
    private UserEntity accountRep;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @Column(name = "target", nullable = false, scale = 3, precision = 15)
    private BigDecimal target;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clientInfoDep", cascade = CascadeType.ALL)
    private List<ClientContactEntity> listContacts;

    public List<ClientContactEntity> getListContacts() {
        if (listContacts == null || listContacts.isEmpty())
            return new ArrayList<>();

        listContacts.sort(Comparator.comparing(ClientContactEntity::isValidMain).reversed());
        return listContacts;
    }
}

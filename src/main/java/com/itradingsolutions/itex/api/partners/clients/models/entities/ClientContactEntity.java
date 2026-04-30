package com.itradingsolutions.itex.api.partners.clients.models.entities;

import com.itradingsolutions.itex.api.partners.common.models.entities.PartnerContactEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "t_clients_contacts")
@Getter
@Setter
public class ClientContactEntity extends PartnerContactEntity {

    @Serial
    private static final long serialVersionUID = 1285658094963082407L;

    @ManyToOne
    @JoinColumn(name = "client_info_dep_id", nullable = false)
    private ClientInfoDepEntity clientInfoDep;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "clientContact", cascade = CascadeType.ALL)
    private List<ClientContactPhoneEntity> listPhones;

    public List<ClientContactPhoneEntity> getListPhones() {
        if (listPhones == null || listPhones.isEmpty())
            return new ArrayList<>();
        listPhones.sort(Comparator.comparing(ClientContactPhoneEntity::isValidMain).reversed());
        return listPhones;
    }
}

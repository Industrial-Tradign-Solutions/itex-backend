package com.itradingsolutions.itex.api.partners.clients.models.entities;

import com.itradingsolutions.itex.api.partners.common.models.entities.PartnerContactPhoneEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Entity
@Table(name = "t_clients_contacts_phones")
@Getter
@Setter
public class ClientContactPhoneEntity extends PartnerContactPhoneEntity {

    @Serial
    private static final long serialVersionUID = -8292080689766113095L;

    @ManyToOne
    @JoinColumn(name = "client_contact_id", nullable = false)
    private ClientContactEntity clientContact;

}

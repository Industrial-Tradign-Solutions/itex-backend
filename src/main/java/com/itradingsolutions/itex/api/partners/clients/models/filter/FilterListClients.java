package com.itradingsolutions.itex.api.partners.clients.models.filter;

import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;
import com.itradingsolutions.itex.api.partners.common.models.filter.PartnerFilter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;


@Getter
@Setter
public class FilterListClients extends PartnerFilter<ClientStatus, ClientEntity> {

    private String code;

    public Specification<ClientEntity> filterClient() {
        Specification<ClientEntity> spec = filter();

        if (getCode() != null && !getCode().isEmpty() && getCode().length() >= 3)
            spec = spec.and(hasCode());

        return spec;
    }

    private Specification<ClientEntity> hasCode() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("code"), getCode().toUpperCase());
    }

}

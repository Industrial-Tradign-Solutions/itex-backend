package com.itradingsolutions.itex.api.ip.q.models.filters;

import com.itradingsolutions.itex.api.common.models.enums.FilterDate;
import com.itradingsolutions.itex.api.common.util.models.filter.BaseFilter;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

@Getter
@Setter
public class FilterListIpQuotation extends BaseFilter<IpQuotationEntity> {

    private String number;
    private UUID clientId;
    private String remarks;
    private IpQuotationStatus status;
    private UUID salesRepId;

    public Specification<IpQuotationEntity> filter() {
        Specification<IpQuotationEntity> spec = Specification.where(null);

        if (getNumber() != null && !getNumber().isBlank())
            spec = spec.and(hasNumber());

        if (getClientId() != null)
            spec = spec.and(hasClientId());

        if (getRemarks() != null && !getRemarks().isBlank())
            spec = spec.and(hasRemarks());

        if (getStatus() != null)
            spec = spec.and(hasStatus());

        if (getSalesRepId() != null)
            spec = spec.and(hasSalesRepId());

        if (getDate() != null) {
            if (getDate().equals(FilterDate.ALL)) {
                if (getInitDate() != null)
                    spec = spec.and(hasInitDate());

                if (getEndDate() != null)
                    spec = spec.and(hasEndDate());
            } else {
                spec = spec.and(hasDate());
            }
        }

        return spec;
    }

    private Specification<IpQuotationEntity> hasNumber() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("number"), getNumber());
    }

    private Specification<IpQuotationEntity> hasClientId() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("client").get("id"), getClientId());
    }

    private Specification<IpQuotationEntity> hasRemarks() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("remarks")), "%" + getRemarks().toLowerCase() + "%");
    }

    private Specification<IpQuotationEntity> hasStatus() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), getStatus());
    }

    private Specification<IpQuotationEntity> hasSalesRepId() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("salesRep").get("id"), getSalesRepId());
    }
}

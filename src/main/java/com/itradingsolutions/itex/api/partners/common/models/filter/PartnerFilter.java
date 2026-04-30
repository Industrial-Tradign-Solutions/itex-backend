package com.itradingsolutions.itex.api.partners.common.models.filter;

import com.itradingsolutions.itex.api.common.util.models.filter.BaseFilter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

@Getter
@Setter
public abstract class PartnerFilter<S, E>  extends BaseFilter<E> {

    private String name;
    private String notes;
    private UUID cityId;
    private S status;
    private UUID countryId;
    private String taxId;

    protected Specification<E> filter() {
        Specification<E> spec = Specification.where(null);

        if (getName() != null && !getName().isEmpty() && getName().length() >= 3)
            spec = spec.and(hasName());

        if (getNotes() != null && !getNotes().isEmpty() && getNotes().length() >= 3)
            spec = spec.and(hasNotes());

        if (getCityId() != null)
            spec = spec.and(hasCity());

        if (getStatus() != null)
            spec = spec.and(hasStatus());

        if (getCountryId() != null )
            spec = spec.and(hasCountry());

        if (getTaxId() != null )
            spec = spec.and(hasTaxId());

        return spec;
    }

    private Specification<E> hasName() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%" + getName().toUpperCase() + "%");
    }

    private Specification<E> hasNotes() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("notes")), "%" + getNotes().toLowerCase() + "%");
    }

    private Specification<E> hasCity() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("city").get("id"), getCityId());
    }

    private Specification<E> hasStatus() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), getStatus());
    }

    private Specification<E> hasCountry() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("city").get("state").get("country").get("id"), getCountryId());
    }

    private Specification<E> hasTaxId() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("taxId"), getTaxId());
    }
}

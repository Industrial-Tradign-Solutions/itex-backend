package com.itradingsolutions.itex.api.ip.products.models.filter;

import com.itradingsolutions.itex.api.common.util.models.filter.BaseFilter;
import com.itradingsolutions.itex.api.ip.products.models.entity.IpProductEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

@Getter
@Setter
public class FilterListIpProducts extends BaseFilter<IpProductEntity> {


    private UUID brandId;
    private String description;
    private String mfrReference;
    private Integer nmfc;
    private String notesKeywords;
    private FreightClass freightClass;
    private IpProductStatus status;

    public Specification<IpProductEntity> filter() {
        Specification<IpProductEntity> spec = Specification.where(null);

        if (getDescription() != null && !getDescription().isEmpty() && getDescription().length() >= 3)
            spec = spec.and(hasDescription());

        if (getMfrReference() != null)
            spec = spec.and(hasMfrReference());

        if (getStatus() != null)
            spec = spec.and(hasStatus());

        if (getFreightClass() != null)
            spec = spec.and(hasFreightClass());

        if (getBrandId() != null )
            spec = spec.and(hasBrand());

        if (getNmfc() != null )
            spec = spec.and(hasNmfc());

        if (getNotesKeywords() != null )
            spec = spec.and(hasNotesOrKeywords());

        return spec;
    }

    private Specification<IpProductEntity> hasDescription() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("description"), "%" + getDescription().toUpperCase() + "%");
    }

    private Specification<IpProductEntity> hasNotesOrKeywords() {
        return (root, query, cb) -> {
            String likePattern = "%" + getNotesKeywords().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("notes")), likePattern),
                    cb.like(cb.lower(root.get("keywords")), likePattern)
            );
        };
    }

    private Specification<IpProductEntity> hasStatus() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), getStatus());
    }

    private Specification<IpProductEntity> hasFreightClass() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("freightClass"), getFreightClass());
    }

    private Specification<IpProductEntity> hasMfrReference() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("mfrReference"), getMfrReference());
    }

    private Specification<IpProductEntity> hasBrand() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("brand").get("id"), getBrandId());
    }

    private Specification<IpProductEntity> hasNmfc() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("nmfc"), getNmfc());
    }
}

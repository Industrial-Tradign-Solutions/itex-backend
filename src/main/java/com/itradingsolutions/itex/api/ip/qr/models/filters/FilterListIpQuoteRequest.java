package com.itradingsolutions.itex.api.ip.qr.models.filters;

import com.itradingsolutions.itex.api.common.models.enums.FilterDate;
import com.itradingsolutions.itex.api.common.util.models.filter.BaseFilter;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestProductEntity;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

@Getter
@Setter
public class FilterListIpQuoteRequest extends BaseFilter<IpQuoteRequestEntity> {

    private String number;
    private UUID clientId;
    private UUID supplierId;
    private String remarks;
    private IpQuoteRequestStatus status;
    private UUID salesRepId;
    private String clientRef;
    private String supplierRef;
    private String productDescription;

    public Specification<IpQuoteRequestEntity> filter() {
        Specification<IpQuoteRequestEntity> spec = Specification.where(null);

        if (getNumber() != null && !getNumber().isBlank())
            spec = spec.and(hasNumber());

        if (getClientId() != null)
            spec = spec.and(hasClientId());

        if (getSupplierId() != null)
            spec = spec.and(hasSupplierId());

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

        if (getProductDescription() != null && !getProductDescription().isBlank())
            spec = spec.and(hasProductDescriptionLike());

        if (getClientRef() != null && !getClientRef().isBlank())
            spec = spec.and(hasProductClientRefLike());

        if (getSupplierRef() != null && !getSupplierRef().isBlank())
            spec = spec.and(hasProductSupplierRefLike());

        return spec;
    }

    private Specification<IpQuoteRequestEntity> hasNumber() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("number"), getNumber());
    }

    private Specification<IpQuoteRequestEntity> hasClientId() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("client").get("id"), getClientId());
    }

    private Specification<IpQuoteRequestEntity> hasSupplierId() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("supplier").get("id"), getSupplierId());
    }

    private Specification<IpQuoteRequestEntity> hasRemarks() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("remarks")), "%" + getRemarks().toLowerCase() + "%");
    }

    private Specification<IpQuoteRequestEntity> hasStatus() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), getStatus());
    }

    private Specification<IpQuoteRequestEntity> hasSalesRepId() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("salesRep").get("id"), getSalesRepId());
    }

    private Specification<IpQuoteRequestEntity> hasProductDescriptionLike() {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);
            Join<IpQuoteRequestEntity, IpQuoteRequestProductEntity> productJoin = root.join("products", JoinType.INNER);

            Expression<String> clientDesc = cb.upper(productJoin.get("ipProduct").get("clientDescription"));
            Expression<String> supplierDesc = cb.upper(productJoin.get("ipProduct").get("description"));
            String pattern = "%" + getProductDescription().toUpperCase() + "%";

            return cb.or(
                    cb.like(clientDesc, pattern),
                    cb.like(supplierDesc, pattern)
            );
        };
    }

    private Specification<IpQuoteRequestEntity> hasProductClientRefLike() {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);
            Join<IpQuoteRequestEntity, IpQuoteRequestProductEntity> productJoin = root.join("products", JoinType.INNER);
            return cb.like(
                    cb.upper(productJoin.get("ipProduct").get("clientReference")),
                    "%" + getClientRef().toUpperCase() + "%"
            );
        };
    }

    private Specification<IpQuoteRequestEntity> hasProductSupplierRefLike() {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);
            Join<IpQuoteRequestEntity, IpQuoteRequestProductEntity> productJoin = root.join("products", JoinType.INNER);
            return cb.like(
                    cb.upper(productJoin.get("ipProduct").get("mfrReference")),
                    "%" + getSupplierRef().toUpperCase() + "%"
            );
        };
    }
}

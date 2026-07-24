package com.itradingsolutions.itex.api.ip.po.models.filters;

import com.itradingsolutions.itex.api.common.models.enums.FilterDate;
import com.itradingsolutions.itex.api.common.util.models.filter.BaseFilter;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderEntity;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderProductEntity;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderStatus;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestProductEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class FilterListIpPurchaseOrder extends BaseFilter<IpPurchaseOrderEntity> {

    private String number;
    private UUID clientId;
    private UUID supplierId;
    private String remarks;
    private IpPurchaseOrderStatus status;
    private UUID salesRepId;
    private String clientRef;
    private String supplierRef;
    private String productDescription;

    public Specification<IpPurchaseOrderEntity> filter() {
        Specification<IpPurchaseOrderEntity> spec = Specification.where(null);

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

        if (hasAnyProductFilter())
            spec = spec.and(hasProductFilter());

        return spec;
    }

    private boolean hasAnyProductFilter() {
        return (getProductDescription() != null && !getProductDescription().isBlank())
                || (getClientRef() != null && !getClientRef().isBlank())
                || (getSupplierRef() != null && !getSupplierRef().isBlank());
    }

    private Specification<IpPurchaseOrderEntity> hasNumber() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("number"), getNumber());
    }

    private Specification<IpPurchaseOrderEntity> hasClientId() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("client").get("id"), getClientId());
    }

    private Specification<IpPurchaseOrderEntity> hasSupplierId() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("supplier").get("id"), getSupplierId());
    }

    private Specification<IpPurchaseOrderEntity> hasRemarks() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("remarks")), "%" + getRemarks().toLowerCase() + "%");
    }

    private Specification<IpPurchaseOrderEntity> hasStatus() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), getStatus());
    }

    private Specification<IpPurchaseOrderEntity> hasSalesRepId() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("salesRep").get("id"), getSalesRepId());
    }

    private Specification<IpPurchaseOrderEntity> hasProductFilter() {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);

            var productJoin = root.join("products", JoinType.INNER);
            var quotationProductJoin = productJoin.join("quotationProduct", JoinType.INNER);
            var qrProductJoin = quotationProductJoin.join("quoteRequestProduct", JoinType.INNER);
            var ipProduct = qrProductJoin.get("ipProduct");

            List<Predicate> predicates = new ArrayList<>();

            if (getProductDescription() != null && !getProductDescription().isBlank())
                predicates.add(buildProductDescriptionPredicate(cb, ipProduct));

            if (getClientRef() != null && !getClientRef().isBlank())
                predicates.add(buildClientRefPredicate(cb, ipProduct));

            if (getSupplierRef() != null && !getSupplierRef().isBlank())
                predicates.add(buildSupplierRefPredicate(cb, ipProduct));

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Predicate buildProductDescriptionPredicate(CriteriaBuilder cb, Path<Object> ipProduct) {
        var clientDesc = cb.upper(ipProduct.get("clientDescription"));
        var supplierDesc = cb.upper(ipProduct.get("description"));
        var pattern = "%" + getProductDescription().toUpperCase() + "%";
        return cb.or(
                cb.like(clientDesc, pattern),
                cb.like(supplierDesc, pattern)
        );
    }

    private Predicate buildClientRefPredicate(CriteriaBuilder cb, Path<Object> ipProduct) {
        return cb.like(
                cb.upper(ipProduct.get("clientReference")),
                "%" + getClientRef().toUpperCase() + "%"
        );
    }

    private Predicate buildSupplierRefPredicate(CriteriaBuilder cb, Path<Object> ipProduct) {
        return cb.like(
                cb.upper(ipProduct.get("mfrReference")),
                "%" + getSupplierRef().toUpperCase() + "%"
        );
    }
}

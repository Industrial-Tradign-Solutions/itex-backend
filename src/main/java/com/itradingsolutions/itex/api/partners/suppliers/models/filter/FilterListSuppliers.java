package com.itradingsolutions.itex.api.partners.suppliers.models.filter;

import com.itradingsolutions.itex.api.masters.brand.models.entities.BrandSupplierEntity;
import com.itradingsolutions.itex.api.partners.common.models.filter.PartnerFilter;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.enums.SupplierStatus;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
public class FilterListSuppliers extends PartnerFilter<SupplierStatus, SupplierEntity> {

    private String brand;

    public Specification<SupplierEntity> filterSuppliers() {
        Specification<SupplierEntity> spec = filter();

        if (getBrand() != null && !getBrand().isBlank())
            spec = spec.and(hasBrand());

        return spec;
    }


    private Specification<SupplierEntity> hasBrand() {
        return (root, query, cb) -> {
            assert query != null;
            query.distinct(true);
            Join<SupplierEntity, BrandSupplierEntity> brandsJoin = root.join("brands", JoinType.INNER);

            Expression<String> brandDesc = cb.upper(brandsJoin.get("brand").get("name"));
            String pattern = "%" + getBrand().toUpperCase().trim() + "%";

            return cb.like(brandDesc, pattern);
        };
    }
}

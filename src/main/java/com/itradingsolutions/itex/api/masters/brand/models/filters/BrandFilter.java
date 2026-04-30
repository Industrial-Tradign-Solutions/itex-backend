package com.itradingsolutions.itex.api.masters.brand.models.filters;

import com.itradingsolutions.itex.api.common.util.models.filter.BaseFilter;
import com.itradingsolutions.itex.api.masters.brand.models.entities.BrandEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
public class BrandFilter extends BaseFilter<BrandEntity> {

    private String name;

    public Specification<BrandEntity> filterBrand() {
        Specification<BrandEntity> spec = Specification.where(null);

        if (getName() != null && !getName().isEmpty())
            spec = spec.and(hasName());

        return spec;
    }

    private Specification<BrandEntity> hasName() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("name"), "%" + getName().toUpperCase() + "%");
    }
}

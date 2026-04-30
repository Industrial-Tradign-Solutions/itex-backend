package com.itradingsolutions.itex.api.partners.suppliers.models.filter;

import com.itradingsolutions.itex.api.partners.common.models.filter.PartnerFilter;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.enums.SupplierStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
public class FilterListSuppliers extends PartnerFilter<SupplierStatus, SupplierEntity> {



    public Specification<SupplierEntity> filterSuppliers() {
        return filter();
    }


}

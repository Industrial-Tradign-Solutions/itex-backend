package com.itradingsolutions.itex.api.partners.suppliers.models.dto;

import com.itradingsolutions.itex.api.partners.common.models.dto.PartnerDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.enums.SupplierStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SupplierDTO extends  PartnerDTO<SupplierStatus, SupplierInfoDepDTO>{

    private String wireAchInstructions;

    private List<String> brands;
}

package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderProductDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PurchaseOrderProductMapper {

    PurchaseOrderProductDTO entityToDto(PurchaseOrderProductEntity entity);
}

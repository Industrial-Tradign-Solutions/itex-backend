package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderProductDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderProductEntity;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpPurchaseOrderProductMapper {

    IpPurchaseOrderProductDTO entityToDto(IpPurchaseOrderProductEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    IpPurchaseOrderProductEntity clone(IpPurchaseOrderProductEntity entity);

    IpPurchaseOrderProductResponse dtoToResponse(IpPurchaseOrderProductDTO dto);
}

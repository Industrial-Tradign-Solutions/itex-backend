package com.itradingsolutions.itex.api.ip.products.models.mappers;

import com.itradingsolutions.itex.api.ip.products.models.dto.IpImportProductDTO;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductDTO;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductSurplusDTO;
import com.itradingsolutions.itex.api.ip.products.models.entity.IpProductEntity;
import com.itradingsolutions.itex.api.ip.products.models.requests.IpProductAddSurplusRequest;
import com.itradingsolutions.itex.api.ip.products.models.requests.IpProductOutSurplusRequest;
import com.itradingsolutions.itex.api.ip.products.models.requests.IpProductRequest;
import com.itradingsolutions.itex.api.ip.products.models.requests.IpProductsImportRequest;
import com.itradingsolutions.itex.api.ip.products.models.responses.BasicIpProductResponse;
import com.itradingsolutions.itex.api.ip.products.models.responses.IpProductImportResponse;
import com.itradingsolutions.itex.api.ip.products.models.responses.IpProductResponse;
import com.itradingsolutions.itex.api.ip.products.models.responses.ListIpProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpProductMapper {
    IpProductDTO entityToDTO(IpProductEntity entity);
    IpProductResponse dtoToResponse(IpProductDTO dto);
    IpProductDTO requestToDto(IpProductRequest request);
    ListIpProductResponse dtoToListResponse(IpProductDTO dto);
    IpProductSurplusDTO addRequestToDTO(IpProductAddSurplusRequest request);
    IpProductSurplusDTO outRequestToDTO(IpProductOutSurplusRequest request);
    BasicIpProductResponse dtoToBasicResponse(IpProductDTO dto);

    IpImportProductDTO importToDTO(IpProductsImportRequest request);
    IpProductImportResponse dtoToImportResponse(IpImportProductDTO dto);
    IpImportProductDTO clone(IpImportProductDTO dto);

}

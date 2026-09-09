package com.itradingsolutions.itex.api.ip.products.services;

import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpImportProductDTO;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductDTO;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductSurplusDTO;
import com.itradingsolutions.itex.api.ip.products.models.entity.IpProductEntity;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import com.itradingsolutions.itex.api.ip.products.models.filter.FilterListIpProducts;
import com.itradingsolutions.itex.api.ip.products.models.responses.BasicIpProductResponse;
import com.itradingsolutions.itex.api.ip.products.models.responses.ListIpProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IIpProductService {
    IpProductDTO createIpProduct(IpProductDTO ipProductDTO, boolean isImported);
    IpProductDTO findIpProductById(UUID id);
    IpProductDTO updateIpProductById(UUID id, IpProductDTO request);
    IpProductDTO openAndLockIpProduct(UUID ipProductId, OpenAndLockType type);
    void unlockIpProduct(UUID ipProductId);
    List<IpProductDTO> listAllOpenIpProducts(String username);
    List<IpProductDTO> listAllOpenIpProducts();
    IpProductDTO addSurplusIpProduct(UUID idProduct, IpProductSurplusDTO request);
    IpProductDTO outSurplusIpProduct(UUID idProduct, IpProductSurplusDTO request);
    IpProductDTO disableIpProductById(UUID id);
    IpProductDTO changeStatusIpProductById(UUID id, IpProductStatus newStatus);
    IpProductDTO replaceProduct(UUID productId, UUID newProductId);
    Page<ListIpProductResponse> listAllProducts(Pageable pageable, FilterListIpProducts filters);
    List<BasicIpProductResponse> listAllActiveProducts();

    IpProductEntity getProductById(UUID id);

    List<IpImportProductDTO> validateImportProducts(List<IpImportProductDTO> items);

}

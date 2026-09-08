package com.itradingsolutions.itex.api.ip.qr.service.impl;

import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import com.itradingsolutions.itex.api.ip.products.repositories.IIpProductRepository;
import com.itradingsolutions.itex.api.ip.products.services.IIpProductService;
import com.itradingsolutions.itex.api.ip.qr.exceptions.NotExistIpQuoteRequestException;
import com.itradingsolutions.itex.api.ip.qr.exceptions.QrProductExistException;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestProductDTO;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestProductEntity;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.ip.qr.models.mappers.IpQuoteRequestProductMapper;
import com.itradingsolutions.itex.api.ip.qr.repositories.IIpQuoteRequestProductRepository;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestProductService;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IpQuoteRequestProductServiceImpl extends UtilServiceAbs implements IIpQuoteRequestProductService {

    private final IpQuoteRequestProductMapper qrProductMapper;
    private final IIpQuoteRequestProductRepository qrProductRepository;
    private final IIpQuoteRequestService qrService;
    private final IIpProductService productService;
    private final IIpProductRepository ipProductRepository;
    private final IUserService userService;

    @Override
    @Transactional
    public IpQuoteRequestProductDTO createIpQuoteRequestProduct(IpQuoteRequestProductDTO productRequest, UUID qrId) {
        var qr = qrService.getEntityById(qrId);
        qrService.validateEditableStatus(qr);
        var productId = validateAndExtractProductId(productRequest);
        validateProductStatusForQuoteRequest(productId, qr.getStatus());
        if (qrProductRepository.existsProductById(productId, qrId))
            throw new QrProductExistException(simpleMessage("ip.qr.product.exist"));
        var entity = new IpQuoteRequestProductEntity();
        entity.setIpQuoteRequest(qr);
        entity.setNumber(entity.getIpQuoteRequest().getMaxNumberOfProducts());
        return saveQrProduct(productRequest, entity);
    }

    @Override
    @Transactional
    public IpQuoteRequestProductDTO updateIpQuoteRequestProduct(IpQuoteRequestProductDTO productRequest, UUID qrProductId, UUID qrId) {
        var entity = findById(qrProductId, qrId);
        qrService.validateEditableStatus(entity.getIpQuoteRequest());
        var productId = validateAndExtractProductId(productRequest);
        validateProductStatusForQuoteRequest(productId, entity.getIpQuoteRequest().getStatus());
        if (qrProductRepository.existsProductById(productId, qrId, qrProductId))
            throw new QrProductExistException(simpleMessage("ip.qr.product.exist"));
        return saveQrProduct(productRequest, entity);
    }

    private UUID validateAndExtractProductId(IpQuoteRequestProductDTO productRequest) {
        if (productRequest.getIpProduct() == null || productRequest.getIpProduct().getId() == null) {
            throw new NotExistIpQuoteRequestException(simpleMessage("ip.product.not-exist"));
        }
        return productRequest.getIpProduct().getId();
    }

    @Override
    @Transactional(readOnly = true)
    public IpQuoteRequestProductDTO getIpQuoteRequestProduct(UUID qrProductId, UUID qrId) {
        return qrProductMapper.entityToDto(findById(qrProductId, qrId));
    }

    @Override
    @Transactional
    public void removeIpQuoteRequestProduct(UUID qrProductId, UUID qrId) {
        qrService.validateEditableStatus(qrService.getEntityById(qrId));
        qrProductRepository.deleteProductById(qrId, qrProductId);
    }

    private IpQuoteRequestProductDTO saveQrProduct(IpQuoteRequestProductDTO productRequest, IpQuoteRequestProductEntity entity) {
        qrService.validateOpenQR(entity.getIpQuoteRequest(), userService.getUserAuthenticated());
        entity.setQuantity(productRequest.getQuantity());
        entity.setUnitType(productRequest.getUnitType());
        entity.setLeadTime(productRequest.getLeadTime());
        entity.setUnitPrice(productRequest.getUnitPrice());
        entity.setLeadTimeType(productRequest.getLeadTimeType());
        entity.setIpProduct(productService.getProductById(productRequest.getIpProduct().getId()));
        return qrProductMapper.entityToDto(qrProductRepository.save(entity));
    }

    private void validateProductStatusForQuoteRequest(UUID productId, IpQuoteRequestStatus qrStatus) {
        var statuses = ipProductRepository.fetchStatusByIds(Set.of(productId));
        if (statuses.isEmpty()) {
            throw new NotExistIpQuoteRequestException(simpleMessage("ip.product.not-exist"));
        }

        var productStatus = statuses.get(0).status();
        if (productStatus == IpProductStatus.INACTIVE) {
            throw new QrProductExistException(simpleMessage("ip.qr.product.inactive-not-allowed"));
        }

        if (qrStatus == IpQuoteRequestStatus.ANSWERED && productStatus != IpProductStatus.ACTIVE) {
            throw new QrProductExistException(compositeMessage(
                    "ip.qr.product.draft-not-allowed", new String[]{qrStatus.name()}));
        }
    }

    private IpQuoteRequestProductEntity findById(UUID qrProductId, UUID qrId) {
        return qrProductRepository.fetchOneById(qrProductId, qrId).orElseThrow(() -> new NotExistIpQuoteRequestException(simpleMessage("ip.qr.not-exist")));
    }
}

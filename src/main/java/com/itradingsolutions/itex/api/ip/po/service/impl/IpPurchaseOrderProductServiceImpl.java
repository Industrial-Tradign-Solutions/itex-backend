package com.itradingsolutions.itex.api.ip.po.service.impl;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.po.exceptions.IpPurchaseOrderNotEditableException;
import com.itradingsolutions.itex.api.ip.po.exceptions.IpPurchaseOrderProductNotEligibleException;
import com.itradingsolutions.itex.api.ip.po.exceptions.NotExistIpPurchaseOrderException;
import com.itradingsolutions.itex.api.ip.po.exceptions.NotOpenIpPurchaseOrderException;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderProductDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderEntity;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderProductEntity;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderStatus;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderProductMapper;
import com.itradingsolutions.itex.api.ip.po.models.response.EligibleIpPurchaseOrderProductResponse;
import com.itradingsolutions.itex.api.ip.po.repository.IIpPurchaseOrderProductRepository;
import com.itradingsolutions.itex.api.ip.po.repository.IIpPurchaseOrderRepository;
import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderProductService;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationProductMapper;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationProductRepository;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IpPurchaseOrderProductServiceImpl extends UtilServiceAbs implements IIpPurchaseOrderProductService {

    private final IIpPurchaseOrderRepository poRepository;
    private final IIpPurchaseOrderProductRepository productRepository;
    private final IIpQuotationProductRepository qProductRepository;
    private final IpQuotationProductMapper quotationProductMapper;
    private final IpPurchaseOrderProductMapper productMapper;
    private final IUserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<EligibleIpPurchaseOrderProductResponse> getEligibleProducts(UUID poId) {
        var po = findEntityById(poId);
        return Optional.ofNullable(po.getQuotation())
                .flatMap(quotation -> Optional.ofNullable(po.getSupplier())
                        .map(supplier -> buildEligibleList(po, quotation, supplier)))
                .orElseGet(List::of);
    }

    @Override
    @Transactional
    public List<IpPurchaseOrderProductDTO> addProducts(UUID poId, List<UUID> quotationProductIds) {
        var po = findEntityById(poId);
        validateOpenPO(po, userService.getUserAuthenticated());
        validateEditable(po);

        var quotation = Optional.ofNullable(po.getQuotation())
                .orElseThrow(() -> new BadRequestException(simpleMessage("ip.po.product.requires-quotation-and-supplier")));
        var supplier = Optional.ofNullable(po.getSupplier())
                .orElseThrow(() -> new BadRequestException(simpleMessage("ip.po.product.requires-quotation-and-supplier")));

        var uniqueRequestedIds = Set.copyOf(quotationProductIds);
        if (uniqueRequestedIds.size() != quotationProductIds.size())
            throw new BadRequestException(simpleMessage("ip.po.product.duplicate-in-request"));

        var eligibleIds = qProductRepository
                .findByQuotationsQuoteRequest_Quotation_IdAndQuotationsQuoteRequest_QuoteRequest_Supplier_Id(
                        quotation.getId(), supplier.getId())
                .stream()
                .collect(Collectors.toMap(IpQuotationProductEntity::getId, entity -> entity));

        var existingIds = productRepository.findQuotationProductIdsByPurchaseOrderId(poId);

        var nextNumber = productRepository.findMaxNumberByPurchaseOrderId(poId) + 1;
        var entities = new ArrayList<IpPurchaseOrderProductEntity>();
        for (var quotationProductId : quotationProductIds) {
            if (!eligibleIds.containsKey(quotationProductId))
                throw new IpPurchaseOrderProductNotEligibleException(simpleMessage("ip.po.product.not-eligible"));
            if (existingIds.contains(quotationProductId)) continue;

            var entity = new IpPurchaseOrderProductEntity();
            entity.setPurchaseOrder(po);
            entity.setQuotationProduct(eligibleIds.get(quotationProductId));
            entity.setNumber(nextNumber++);
            entities.add(entity);
        }

        if (entities.isEmpty()) return List.of();

        return productRepository.saveAll(entities).stream()
                .map(productMapper::entityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IpPurchaseOrderProductDTO get(UUID id, UUID poId) {
        return productMapper.entityToDto(findProductById(id, poId));
    }

    @Override
    @Transactional
    public void remove(UUID id, UUID poId) {
        var po = findEntityById(poId);
        validateOpenPO(po, userService.getUserAuthenticated());
        validateEditable(po);
        var product = findProductById(id, poId);
        productRepository.delete(product);
    }

    private IpPurchaseOrderProductEntity findProductById(UUID id, UUID poId) {
        return productRepository.findByIdAndPurchaseOrder_Id(id, poId)
                .orElseThrow(() -> new NotExistIpPurchaseOrderException(simpleMessage("ip.po.product.not-exist")));
    }

    private List<EligibleIpPurchaseOrderProductResponse> buildEligibleList(IpPurchaseOrderEntity po,
                                                                            IpQuotationEntity quotation,
                                                                            SupplierEntity supplier) {
        var existingIds = productRepository.findQuotationProductIdsByPurchaseOrderId(po.getId());
        return qProductRepository
                .findByQuotationsQuoteRequest_Quotation_IdAndQuotationsQuoteRequest_QuoteRequest_Supplier_Id(
                        quotation.getId(), supplier.getId())
                .stream()
                .filter(qp -> !existingIds.contains(qp.getId()))
                .map(this::toEligibleResponse)
                .toList();
    }

    private EligibleIpPurchaseOrderProductResponse toEligibleResponse(IpQuotationProductEntity entity) {
        var dto = quotationProductMapper.entityToDto(entity);
        var qrProduct = dto.getQuoteRequestProduct();
        var ipProduct = qrProduct != null ? qrProduct.getIpProduct() : null;
        return new EligibleIpPurchaseOrderProductResponse(
                dto.getId(),
                ipProduct != null ? ipProduct.getDescription() : null,
                ipProduct != null ? ipProduct.getMfrReference() : null,
                qrProduct != null ? qrProduct.getQuantity() : null,
                dto.getSellingUnitPrice(),
                dto.getSellingExtendedPrice(),
                dto.getQrNumber()
        );
    }

    private IpPurchaseOrderEntity findEntityById(UUID id) {
        return poRepository.findById(id).orElseThrow(
                () -> new NotExistIpPurchaseOrderException(simpleMessage("ip.po.not-exist")));
    }

    private void validateOpenPO(IpPurchaseOrderEntity entity, UserEntity userAuthenticated) {
        if (entity.getOpenBy() == null)
            throw new NotOpenIpPurchaseOrderException(simpleMessage("ip.po.not-block"));
        if (!entity.getOpenBy().getId().equals(userAuthenticated.getId()))
            throw new NotOpenIpPurchaseOrderException(
                    compositeMessage("ip.po.not-block-by", new String[]{entity.getOpenBy().getFullName()}));
    }

    private void validateEditable(IpPurchaseOrderEntity po) {
        Optional.of(po.getStatus())
                .filter(status -> !isOpenStatus(status))
                .ifPresent(status -> {
                    throw new IpPurchaseOrderNotEditableException(simpleMessage("ip.po.not-editable"));
                });
    }

    private static boolean isOpenStatus(IpPurchaseOrderStatus status) {
        return status == IpPurchaseOrderStatus.CREATED
                || status == IpPurchaseOrderStatus.SENT
                || status == IpPurchaseOrderStatus.ANSWERED;
    }
}

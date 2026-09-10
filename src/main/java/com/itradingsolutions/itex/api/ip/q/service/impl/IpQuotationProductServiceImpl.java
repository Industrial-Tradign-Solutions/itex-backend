package com.itradingsolutions.itex.api.ip.q.service.impl;

import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import com.itradingsolutions.itex.api.ip.products.repositories.IIpProductRepository;
import com.itradingsolutions.itex.api.ip.q.exceptions.NotExistIpQuotationException;
import com.itradingsolutions.itex.api.ip.q.exceptions.QProductExistException;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationProductMapper;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationProductRepository;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationsQuoteRequestRepository;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationProductService;
import com.itradingsolutions.itex.api.ip.q.service.IpQuotationService;
import com.itradingsolutions.itex.api.ip.qr.models.dto.QuoteRequestProductIdProjection;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestProductEntity;
import com.itradingsolutions.itex.api.ip.qr.repositories.IIpQuoteRequestProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IpQuotationProductServiceImpl extends UtilServiceAbs implements IIpQuotationProductService {

    private final IpQuotationProductMapper qProductMapper;
    private final IIpQuotationProductRepository qProductRepository;
    private final IIpQuotationsQuoteRequestRepository qqrRepository;
    private final IIpQuoteRequestProductRepository qrProductRepository;
    private final IIpProductRepository ipProductRepository;
    private final IpQuotationService quotationService;
    private final IUserService userService;

    @Override
    @Transactional
    public List<IpQuotationProductDTO> createIpQuotationProducts(List<IpQuotationProductDTO> productRequests, UUID quotationId) {
        if (productRequests.isEmpty()) {
            return List.of();
        }

        quotationService.validateQuotationEditable(
                quotationService.getEntityById(quotationId),
                userService.getUserAuthenticated()
        );

        // 1. Validate no duplicate quoteRequestProductId within request
        var qrProductIds = productRequests.stream()
                .map(dto -> dto.getQuoteRequestProduct().getId())
                .toList();
        var uniqueQrProductIds = Set.copyOf(qrProductIds);
        if (uniqueQrProductIds.size() != qrProductIds.size()) {
            throw new BadRequestException(simpleMessage("ip.q.product.duplicate-qrproduct-in-request"));
        }

        // 2. Bulk load quoteRequestProduct → productId mapping
        var idToProductId = qrProductRepository.findProductIdsByIds(uniqueQrProductIds)
                .stream()
                .collect(Collectors.toMap(
                        QuoteRequestProductIdProjection::id,
                        QuoteRequestProductIdProjection::productId
                ));

        if (idToProductId.size() != uniqueQrProductIds.size()) {
            throw new NotExistIpQuotationException(simpleMessage("ip.q.not-exist"));
        }

        // 2.1 Block products not in ACTIVE status
        var notActiveProducts = ipProductRepository.fetchStatusByIds(Set.copyOf(idToProductId.values()))
                .stream()
                .filter(projection -> projection.status() != IpProductStatus.ACTIVE)
                .toList();
        if (!notActiveProducts.isEmpty()) {
            throw new BadRequestException(simpleMessage("ip.q.product.not-active-create"));
        }

        // 3. Validate no duplicate productId within request
        var seenProductIds = new HashSet<UUID>();
        for (var dto : productRequests) {
            var productId = idToProductId.get(dto.getQuoteRequestProduct().getId());
            if (!seenProductIds.add(productId)) {
                throw new BadRequestException(simpleMessage("ip.q.product.product-already-in-quotation"));
            }
        }

        // 4. Load existing state from DB (one query each)
        var existingQrProductIds = qProductRepository.findQuoteRequestProductIdsByQuotationId(quotationId);
        var existingProductIds = qProductRepository.findExistingProductIdsByQuotationId(quotationId);

        // 5. Bulk-load supporting entities
        var qqrIds = productRequests.stream()
                .map(IpQuotationProductDTO::getQuotationsQuoteRequestId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        var qqrById = qqrRepository.findByQuotationIdAndIdsIn(quotationId, qqrIds)
                .stream()
                .collect(Collectors.toMap(
                        IpQuotationsQuoteRequestEntity::getId,
                        Function.identity()
                ));

        var qrProductById = qrProductRepository.findByIdsWithProduct(uniqueQrProductIds)
                .stream()
                .collect(Collectors.toMap(
                        IpQuoteRequestProductEntity::getId,
                        Function.identity()
                ));

        // 6. Validate & build entities
        var entities = new ArrayList<IpQuotationProductEntity>();
        var nextNumberByQqr = new HashMap<UUID, Integer>();

        for (var dto : productRequests) {
            var qrProductId = dto.getQuoteRequestProduct().getId();
            var productId = idToProductId.get(qrProductId);

            // Already exists in quotation by quoteRequestProductId -> skip
            if (existingQrProductIds.contains(qrProductId)) {
                continue;
            }

            // Same underlying product already in quotation -> error
            if (existingProductIds.contains(productId)) {
                throw new BadRequestException(simpleMessage("ip.q.product.product-already-in-quotation"));
            }

            var qqr = qqrById.get(dto.getQuotationsQuoteRequestId());
            if (qqr == null) {
                throw new NotExistIpQuotationException(simpleMessage("ip.q.not-exist"));
            }

            var qrProductEntity = qrProductById.get(qrProductId);
            if (qrProductEntity == null) {
                throw new NotExistIpQuotationException(simpleMessage("ip.q.not-exist"));
            }

            var entity = new IpQuotationProductEntity();
            entity.setQuotationsQuoteRequest(qqr);


            var qqrId = qqr.getId();
            var nextNumber = nextNumberByQqr.computeIfAbsent(qqrId, id -> qqr.getMaxNumberOfProducts());
            entity.setNumber(nextNumber);
            nextNumberByQqr.put(qqrId, nextNumber + 1);

            entity.setProfitMargin(dto.getProfitMargin());
            entity.setCondition(dto.getCondition());
            entity.setQuoteRequestProduct(qrProductEntity);
            if (dto.getItsLeadTime() == null) {
                log.warn("Add Q product: itsLeadTime null -> 0 | qqrId={} qrProductId={}",
                        qqrId, qrProductId);
            }
            entity.setItsLeadTime(dto.getItsLeadTime());
            log.info("Add Q product | qqrId={} qrProductId={} leadTime={} + itsLeadTime={} -> totalLeadTime={} condition={}",
                    qqrId, qrProductId,
                    Optional.ofNullable(qrProductEntity.getLeadTime()).orElse(0),
                    entity.getItsLeadTime(),
                    Optional.ofNullable(qrProductEntity.getLeadTime()).orElse(0) + entity.getItsLeadTime(),
                    dto.getCondition());

            entities.add(entity);
        }

        if (entities.isEmpty()) {
            return List.of();
        }

        var saved = qProductRepository.saveAll(entities);
        return saved.stream()
                .map(qProductMapper::entityToDto)
                .toList();
    }

    @Override
    @Transactional
    public IpQuotationProductDTO updateIpQuotationProduct(IpQuotationProductDTO productRequest, UUID qProductId, UUID quotationId) {
        var entity = findById(qProductId, quotationId);
        quotationService.validateQuotationEditable(
                entity.getQuotationsQuoteRequest().getQuotation(),
                userService.getUserAuthenticated()
        );

        if (productRequest.getQuoteRequestProduct() != null && productRequest.getQuoteRequestProduct().getId() != null) {
            if (qProductRepository.existsByQuoteRequestProduct_IdAndQuotationsQuoteRequest_IdAndIdNot(
                    productRequest.getQuoteRequestProduct().getId(), entity.getQuotationsQuoteRequest().getId(), qProductId))
                throw new QProductExistException(simpleMessage("ip.q.product.exist"));

            validateUnderlyingProductNotDuplicate(productRequest, entity, quotationId);
        }

        return saveQProduct(productRequest, entity);
    }

    private void validateUnderlyingProductNotDuplicate(IpQuotationProductDTO productRequest,
                                                       IpQuotationProductEntity entity,
                                                       UUID quotationId) {
        var requestedQrProductId = productRequest.getQuoteRequestProduct().getId();
        var idToProductId = qrProductRepository.findProductIdsByIds(Set.of(requestedQrProductId))
                .stream()
                .collect(Collectors.toMap(
                        QuoteRequestProductIdProjection::id,
                        QuoteRequestProductIdProjection::productId
                ));

        if (idToProductId.size() != 1) {
            throw new NotExistIpQuotationException(simpleMessage("ip.q.not-exist"));
        }

        var newProductId = idToProductId.get(requestedQrProductId);
        var currentProductId = Optional.ofNullable(entity.getQuoteRequestProduct())
                .map(qrp -> qrp.getIpProduct().getId())
                .orElse(null);

        if (Objects.equals(newProductId, currentProductId)) {
            return;
        }

        var existingProductIds = qProductRepository.findExistingProductIdsByQuotationId(quotationId);
        if (existingProductIds.contains(newProductId)) {
            throw new BadRequestException(simpleMessage("ip.q.product.product-already-in-quotation"));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public IpQuotationProductDTO getIpQuotationProduct(UUID qProductId, UUID quotationId) {
        return qProductMapper.entityToDto(findById(qProductId, quotationId));
    }

    @Override
    @Transactional
    public void removeIpQuotationProduct(UUID qProductId, UUID quotationId) {
        var entity = findById(qProductId, quotationId);
        quotationService.validateQuotationEditable(
                entity.getQuotationsQuoteRequest().getQuotation(),
                userService.getUserAuthenticated()
        );
        qProductRepository.deleteByIdAndQuotationsQuoteRequest_Quotation_Id(qProductId, quotationId);
    }

    private IpQuotationProductDTO saveQProduct(IpQuotationProductDTO productRequest, IpQuotationProductEntity entity) {
        var oldItsLeadTime = entity.getItsLeadTime();
        entity.setProfitMargin(productRequest.getProfitMargin());
        entity.setCondition(productRequest.getCondition());
        entity.setItsLeadTime(productRequest.getItsLeadTime());

        if (productRequest.getQuoteRequestProduct() != null && productRequest.getQuoteRequestProduct().getId() != null) {
            var qrProduct = qrProductRepository.findById(productRequest.getQuoteRequestProduct().getId())
                    .orElseThrow(() -> new NotExistIpQuotationException(simpleMessage("ip.q.not-exist")));
            if (qrProduct.getIpProduct().getStatus() != IpProductStatus.ACTIVE)
                throw new BadRequestException(simpleMessage("ip.q.product.not-active-create"));
            entity.setQuoteRequestProduct(qrProduct);
        } else {
            entity.setQuoteRequestProduct(null);
        }

        if (productRequest.getItsLeadTime() == null && oldItsLeadTime != null && oldItsLeadTime != 0) {
            log.warn("Edit Q product: itsLeadTime null -> 0 (previous value {}) | qProductId={}",
                    oldItsLeadTime, entity.getId());
        }
        var baseLeadTime = Optional.ofNullable(entity.getQuoteRequestProduct())
                .map(IpQuoteRequestProductEntity::getLeadTime)
                .orElse(0);
        log.info("Edit Q product | qProductId={} leadTime={} + itsLeadTime={} -> totalLeadTime={} condition={}",
                entity.getId(), baseLeadTime, entity.getItsLeadTime(),
                baseLeadTime + entity.getItsLeadTime(), entity.getCondition());

        return qProductMapper.entityToDto(qProductRepository.save(entity));
    }

    private IpQuotationProductEntity findById(UUID qProductId, UUID quotationId) {
        return qProductRepository.findByIdAndQuotationsQuoteRequest_Quotation_Id(qProductId, quotationId)
                .orElseThrow(() -> new NotExistIpQuotationException(simpleMessage("ip.q.not-exist")));
    }
}


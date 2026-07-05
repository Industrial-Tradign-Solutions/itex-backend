package com.itradingsolutions.itex.api.ip.q.service.impl;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.q.exceptions.NotExistIpQuotationException;
import com.itradingsolutions.itex.api.ip.q.exceptions.QProductExistException;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationProductMapper;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationProductRepository;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationsQuoteRequestRepository;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationProductService;
import com.itradingsolutions.itex.api.ip.qr.repositories.IIpQuoteRequestProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IpQuotationProductServiceImpl extends UtilServiceAbs implements IIpQuotationProductService {

    private final IpQuotationProductMapper qProductMapper;
    private final IIpQuotationProductRepository qProductRepository;
    private final IIpQuotationsQuoteRequestRepository qqrRepository;
    private final IIpQuoteRequestProductRepository qrProductRepository;

    @Override
    @Transactional
    public List<IpQuotationProductDTO> createIpQuotationProducts(List<IpQuotationProductDTO> productRequests, UUID quotationId) {
        if (productRequests.isEmpty()) {
            return List.of();
        }

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
                        row -> (UUID) row[0],
                        row -> (UUID) row[1]
                ));

        if (idToProductId.size() != uniqueQrProductIds.size()) {
            throw new NotExistIpQuotationException(simpleMessage("ip.q.not-exist"));
        }

        // 3. Deduplicate by productId within request (first wins, rest skipped)
        var seenProductIds = new HashSet<UUID>();
        var deduplicated = new ArrayList<IpQuotationProductDTO>();
        for (var dto : productRequests) {
            var productId = idToProductId.get(dto.getQuoteRequestProduct().getId());
            if (seenProductIds.add(productId)) {
                deduplicated.add(dto);
            }
        }

        // 4. Load existing state from DB (one query each)
        var existingQrProductIds = qProductRepository.findQuoteRequestProductIdsByQuotationId(quotationId);
        var existingProductIds = qProductRepository.findExistingProductIdsByQuotationId(quotationId);

        // 5. Validate & build entities
        var entities = new ArrayList<IpQuotationProductEntity>();
        var nextNumberByQqr = new HashMap<UUID, Integer>();

        for (var dto : deduplicated) {
            var qrProductId = dto.getQuoteRequestProduct().getId();
            var productId = idToProductId.get(qrProductId);

            // Already exists in quotation by quoteRequestProductId → skip
            if (existingQrProductIds.contains(qrProductId)) {
                continue;
            }

            // Same underlying product already in quotation → error
            if (existingProductIds.contains(productId)) {
                throw new BadRequestException(simpleMessage("ip.q.product.product-already-in-quotation"));
            }

            var qqr = qqrRepository.findByIdAndQuotation_Id(dto.getQuotationsQuoteRequestId(), quotationId)
                    .orElseThrow(() -> new NotExistIpQuotationException(simpleMessage("ip.q.not-exist")));

            var qrProductEntity = qrProductRepository.findById(qrProductId)
                    .orElseThrow(() -> new NotExistIpQuotationException(simpleMessage("ip.q.not-exist")));

            var entity = new IpQuotationProductEntity();
            entity.setQuotationsQuoteRequest(qqr);

            var qqrId = qqr.getId();
            var nextNumber = nextNumberByQqr.computeIfAbsent(qqrId, id -> qqr.getMaxNumberOfProducts());
            entity.setNumber(nextNumber);
            nextNumberByQqr.put(qqrId, nextNumber + 1);

            entity.setProfitMargin(dto.getProfitMargin());
            entity.setCondition(dto.getCondition());
            entity.setQuoteRequestProduct(qrProductEntity);

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

        if (productRequest.getQuoteRequestProduct() != null && productRequest.getQuoteRequestProduct().getId() != null) {
            if (qProductRepository.existsByQuoteRequestProduct_IdAndQuotationsQuoteRequest_IdAndIdNot(
                    productRequest.getQuoteRequestProduct().getId(), entity.getQuotationsQuoteRequest().getId(), qProductId))
                throw new QProductExistException(simpleMessage("ip.q.product.exist"));
        }

        return saveQProduct(productRequest, entity);
    }

    @Override
    @Transactional(readOnly = true)
    public IpQuotationProductDTO getIpQuotationProduct(UUID qProductId, UUID quotationId) {
        return qProductMapper.entityToDto(findById(qProductId, quotationId));
    }

    @Override
    @Transactional
    public void removeIpQuotationProduct(UUID qProductId, UUID quotationId) {
        qProductRepository.deleteByIdAndQuotationsQuoteRequest_Quotation_Id(qProductId, quotationId);
    }

    private IpQuotationProductDTO saveQProduct(IpQuotationProductDTO productRequest, IpQuotationProductEntity entity) {
        entity.setProfitMargin(productRequest.getProfitMargin());
        entity.setCondition(productRequest.getCondition());

        if (productRequest.getQuoteRequestProduct() != null && productRequest.getQuoteRequestProduct().getId() != null) {
            entity.setQuoteRequestProduct(
                    qrProductRepository.findById(productRequest.getQuoteRequestProduct().getId())
                            .orElseThrow(() -> new NotExistIpQuotationException(simpleMessage("ip.q.not-exist")))
            );
        } else {
            entity.setQuoteRequestProduct(null);
        }

        return qProductMapper.entityToDto(qProductRepository.save(entity));
    }

    private IpQuotationProductEntity findById(UUID qProductId, UUID quotationId) {
        return qProductRepository.findByIdAndQuotationsQuoteRequest_Quotation_Id(qProductId, quotationId)
                .orElseThrow(() -> new NotExistIpQuotationException(simpleMessage("ip.q.not-exist")));
    }
}


package com.itradingsolutions.itex.api.ip.q.service.impl;

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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IpQuotationProductServiceImpl extends UtilServiceAbs implements IIpQuotationProductService {

    private final IpQuotationProductMapper qProductMapper;
    private final IIpQuotationProductRepository qProductRepository;
    private final IIpQuotationsQuoteRequestRepository qqrRepository;
    private final IIpQuoteRequestProductRepository qrProductRepository;

    @Override
    @Transactional
    public IpQuotationProductDTO createIpQuotationProduct(IpQuotationProductDTO productRequest, UUID quotationId) {
        var qqr = qqrRepository.findByIdAndQuotation_Id(productRequest.getQuotationsQuoteRequestId(), quotationId)
                .orElseThrow(() -> new NotExistIpQuotationException(simpleMessage("ip.q.not-exist")));

        if (productRequest.getQuoteRequestProduct() != null && productRequest.getQuoteRequestProduct().getId() != null) {
            if (qProductRepository.existsByQuoteRequestProduct_IdAndQuotationsQuoteRequest_Id(
                    productRequest.getQuoteRequestProduct().getId(), qqr.getId()))
                throw new QProductExistException(simpleMessage("ip.q.product.exist"));
        }

        var entity = new IpQuotationProductEntity();
        entity.setQuotationsQuoteRequest(qqr);
        entity.setNumber(qqr.getMaxNumberOfProducts());
        return saveQProduct(productRequest, entity);
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


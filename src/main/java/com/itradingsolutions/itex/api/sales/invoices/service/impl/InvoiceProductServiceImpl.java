package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderService;
import com.itradingsolutions.itex.api.ip.products.models.enums.ProductCondition;
import com.itradingsolutions.itex.api.ip.products.services.IIpProductService;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestProductDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceProductDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceIpProductEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceProductMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.ImportInvoiceProductsRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.request.InvoiceProductRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.response.AvailablePoProductResponse;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceHistoryService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceProductService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAmountCalculator;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceChildMutationSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceProductServiceImpl extends UtilServiceAbs implements IInvoiceProductService {

    private final InvoiceChildMutationSupport support;
    private final IInvoiceRepository repository;
    private final InvoiceProductMapper mapper;
    private final InvoiceAmountCalculator calculator;
    private final IInvoiceHistoryService historyService;
    private final IIpProductService productService;
    private final IIpPurchaseOrderService purchaseOrderService;

    @Override
    @Transactional
    public InvoiceProductDTO add(UUID invoiceId, InvoiceProductRequest request) {
        var invoice = support.loadEditable(invoiceId);
        assertProductNotPresent(invoice, request.productId(), null);

        var dto = mapper.requestToDTO(request);
        var entity = new InvoiceIpProductEntity();
        entity.setInvoice(invoice);
        entity.setProduct(productService.getProductById(request.productId()));
        entity.setNumber(nextNumber(invoice));
        applyLineFields(entity, dto);
        invoice.getProducts().add(entity);

        calculator.applyTotals(invoice);
        repository.save(invoice);

        var savedDto = mapper.entityToDTO(entity);
        historyService.addHistoryProduct(InvoiceHistoryAction.ADD_PRODUCT, null, savedDto, invoiceId);
        return savedDto;
    }

    @Override
    @Transactional
    public InvoiceProductDTO update(UUID invoiceId, UUID productId, InvoiceProductRequest request) {
        var invoice = support.loadEditable(invoiceId);
        var entity = findChild(invoice, productId);
        assertProductNotPresent(invoice, request.productId(), productId);

        var oldDto = mapper.entityToDTO(entity);
        var dto = mapper.requestToDTO(request);
        if (!entity.getProduct().getId().equals(request.productId()))
            entity.setProduct(productService.getProductById(request.productId()));
        applyLineFields(entity, dto);

        calculator.applyTotals(invoice);
        repository.save(invoice);

        var newDto = mapper.entityToDTO(entity);
        historyService.addHistoryProduct(InvoiceHistoryAction.UPDATE_PRODUCT, oldDto, newDto, invoiceId);
        return newDto;
    }

    @Override
    @Transactional
    public void remove(UUID invoiceId, UUID productId) {
        var invoice = support.loadEditable(invoiceId);
        var entity = findChild(invoice, productId);
        var oldDto = mapper.entityToDTO(entity);

        invoice.getProducts().removeIf(p -> p.getId().equals(productId));

        calculator.applyTotals(invoice);
        repository.save(invoice);

        historyService.addHistoryProduct(InvoiceHistoryAction.REMOVE_PRODUCT, oldDto, null, invoiceId);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceProductDTO get(UUID invoiceId, UUID productId) {
        var invoice = support.loadReadable(invoiceId);
        return mapper.entityToDTO(findChild(invoice, productId));
    }

    @Override
    @Transactional
    public List<InvoiceProductDTO> importFromPo(UUID invoiceId, ImportInvoiceProductsRequest request) {
        var invoice = support.loadEditable(invoiceId);
        support.assertPoLinked(invoice, request.poId());

        var po = purchaseOrderService.findById(request.poId());
        var requested = new HashSet<>(request.poProductIds());
        var presentProductIds = new HashSet<UUID>();
        invoice.getProducts().forEach(p -> presentProductIds.add(p.getProduct().getId()));

        var added = new ArrayList<InvoiceIpProductEntity>();
        int number = nextNumber(invoice);

        for (var poProduct : po.getProducts()) {
            if (!requested.contains(poProduct.getId()))
                continue;
            var quotationProduct = poProduct.getQuotationProduct();
            var qrProduct = quotationProduct != null ? quotationProduct.getQuoteRequestProduct() : null;
            if (qrProduct == null || qrProduct.getIpProduct() == null)
                continue;
            var productId = qrProduct.getIpProduct().getId();
            if (!presentProductIds.add(productId))
                continue;

            var entity = new InvoiceIpProductEntity();
            entity.setInvoice(invoice);
            entity.setProduct(productService.getProductById(productId));
            entity.setNumber(number++);
            applyImportedFields(entity, quotationProduct, qrProduct);
            invoice.getProducts().add(entity);
            added.add(entity);
        }

        calculator.applyTotals(invoice);
        repository.save(invoice);

        var results = new ArrayList<InvoiceProductDTO>();
        for (var entity : added) {
            var dto = mapper.entityToDTO(entity);
            results.add(dto);
            historyService.addHistoryProduct(InvoiceHistoryAction.ADD_PRODUCT, null, dto, invoiceId);
        }
        return results;
    }

    private void applyLineFields(InvoiceIpProductEntity entity, InvoiceProductDTO dto) {
        entity.setQuantity(dto.getQuantity());
        entity.setUnitType(dto.getUnitType());
        entity.setLeadTime(dto.getLeadTime() != null ? dto.getLeadTime() : 0);
        entity.setLeadTimeType(dto.getLeadTimeType() != null ? dto.getLeadTimeType() : LeadTime.WEEKS);
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setProfitMargin(dto.getProfitMargin());
        entity.setCondition(dto.getCondition());
    }

    /**
     * Copies the PO line into an invoice line: cost from the Quote Request, margin/condition from
     * the Quotation. The selling price is recomputed later from cost + margin (never stored
     * pre-multiplied) so the margin is not applied twice.
     */
    private void applyImportedFields(InvoiceIpProductEntity entity, IpQuotationProductDTO quotationProduct,
                                     IpQuoteRequestProductDTO qrProduct) {
        entity.setQuantity(qrProduct.getQuantity() != null ? qrProduct.getQuantity() : BigDecimal.ZERO);
        entity.setUnitType(qrProduct.getUnitType() != null ? qrProduct.getUnitType().name() : "UNIT");
        entity.setLeadTime(qrProduct.getLeadTime() != null ? qrProduct.getLeadTime() : 0);
        entity.setLeadTimeType(qrProduct.getLeadTimeType() != null ? qrProduct.getLeadTimeType() : LeadTime.WEEKS);
        entity.setUnitPrice(qrProduct.getUnitPrice() != null ? qrProduct.getUnitPrice() : BigDecimal.ZERO);
        entity.setProfitMargin(quotationProduct.getProfitMargin() != null ? quotationProduct.getProfitMargin() : BigDecimal.ZERO);
        entity.setCondition(quotationProduct.getCondition() != null ? quotationProduct.getCondition() : ProductCondition.NEW);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailablePoProductResponse> listAvailableFromPos(UUID invoiceId) {
        var invoice = support.loadReadable(invoiceId);

        var presentProductIds = new HashSet<UUID>();
        invoice.getProducts().forEach(p -> presentProductIds.add(p.getProduct().getId()));

        var result = new ArrayList<AvailablePoProductResponse>();
        for (var link : Optional.ofNullable(invoice.getLinkedPurchaseOrders()).orElseGet(Collections::emptyList)) {
            var po = purchaseOrderService.findById(link.getPurchaseOrder().getId());
            var poId = link.getPurchaseOrder().getId();
            var poNumber = po.getNumber();

            for (var poProduct : Optional.ofNullable(po.getProducts()).orElseGet(Collections::emptyList)) {
                var quotationProduct = poProduct.getQuotationProduct();
                var qrProduct = quotationProduct != null ? quotationProduct.getQuoteRequestProduct() : null;
                if (qrProduct == null || qrProduct.getIpProduct() == null)
                    continue;
                var productId = qrProduct.getIpProduct().getId();
                if (presentProductIds.contains(productId))
                    continue;

                result.add(new AvailablePoProductResponse(
                        poId,
                        poNumber,
                        poProduct.getId(),
                        productId,
                        qrProduct.getIpProduct().getDescription(),
                        qrProduct.getIpProduct().getMfrReference(),
                        qrProduct.getQuantity() != null ? qrProduct.getQuantity() : BigDecimal.ZERO,
                        qrProduct.getUnitType() != null ? qrProduct.getUnitType().name() : "UNIT",
                        qrProduct.getLeadTime() != null ? qrProduct.getLeadTime() : 0,
                        qrProduct.getLeadTimeType(),
                        qrProduct.getUnitPrice() != null ? qrProduct.getUnitPrice() : BigDecimal.ZERO,
                        quotationProduct.getProfitMargin() != null ? quotationProduct.getProfitMargin() : BigDecimal.ZERO,
                        quotationProduct.getCondition()
                ));
            }
        }
        return result;
    }

    private void assertProductNotPresent(InvoiceEntity invoice, UUID productId, UUID excludeLineId) {
        var duplicate = invoice.getProducts().stream()
                .filter(p -> excludeLineId == null || !p.getId().equals(excludeLineId))
                .anyMatch(p -> p.getProduct().getId().equals(productId));
        if (duplicate)
            throw new BadRequestException(simpleMessage("sales.invoice.product.exist"));
    }

    private InvoiceIpProductEntity findChild(InvoiceEntity invoice, UUID productId) {
        return invoice.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(simpleMessage("sales.invoice.product.not-exist")));
    }

    private int nextNumber(InvoiceEntity invoice) {
        return invoice.getProducts().stream()
                .map(InvoiceIpProductEntity::getNumber)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }
}

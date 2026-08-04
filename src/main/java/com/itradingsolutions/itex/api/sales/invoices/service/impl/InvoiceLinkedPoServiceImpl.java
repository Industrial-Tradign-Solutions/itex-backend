package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderService;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoicePurchaseOrderDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceIpPoEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceLinkedPoMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.LinkInvoicePurchaseOrdersRequest;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceHistoryService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceLinkedPoService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceChildMutationSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceLinkedPoServiceImpl extends UtilServiceAbs implements IInvoiceLinkedPoService {

    private final InvoiceChildMutationSupport support;
    private final IInvoiceRepository repository;
    private final InvoiceLinkedPoMapper mapper;
    private final IIpPurchaseOrderService purchaseOrderService;
    private final IInvoiceHistoryService historyService;

    @Override
    @Transactional
    public List<InvoicePurchaseOrderDTO> link(UUID invoiceId, LinkInvoicePurchaseOrdersRequest request) {
        var invoice = support.loadEditable(invoiceId);

        var seen = new HashSet<UUID>();
        invoice.getLinkedPurchaseOrders().forEach(l -> seen.add(l.getPurchaseOrder().getId()));

        var added = new ArrayList<InvoiceIpPoEntity>();
        for (var poId : new LinkedHashSet<>(request.poIds())) {
            if (!seen.add(poId))
                continue;
            var po = purchaseOrderService.findEntityById(poId);
            var link = new InvoiceIpPoEntity();
            link.setInvoice(invoice);
            link.setPurchaseOrder(po);
            link.setId(invoice.getId(), poId);
            invoice.getLinkedPurchaseOrders().add(link);
            added.add(link);
        }

        // No total recalculation: the PO link is only a traceability record (guide §3).
        repository.save(invoice);

        var results = new ArrayList<InvoicePurchaseOrderDTO>();
        for (var link : added) {
            var dto = mapper.entityToDTO(link);
            results.add(dto);
            historyService.addHistoryPurchaseOrder(InvoiceHistoryAction.ADD_PURCHASE_ORDER, null, dto, invoiceId);
        }
        return results;
    }

    @Override
    @Transactional
    public void unlink(UUID invoiceId, UUID poId) {
        var invoice = support.loadEditable(invoiceId);
        var link = invoice.getLinkedPurchaseOrders().stream()
                .filter(l -> l.getPurchaseOrder().getId().equals(poId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(simpleMessage("sales.invoice.po.not-linked")));

        var oldDto = mapper.entityToDTO(link);
        invoice.getLinkedPurchaseOrders().removeIf(l -> l.getPurchaseOrder().getId().equals(poId));

        repository.save(invoice);

        historyService.addHistoryPurchaseOrder(InvoiceHistoryAction.REMOVE_PURCHASE_ORDER, oldDto, null, invoiceId);
    }
}

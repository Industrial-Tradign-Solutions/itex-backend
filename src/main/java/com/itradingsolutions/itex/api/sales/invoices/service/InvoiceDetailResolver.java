package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceChargeMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceTaxMapper;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceChargeRepository;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceClonedRepository;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceTaxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Assembles the full {@link InvoiceDTO} — the four collections {@code entityToDTO} deliberately
 * ignores — for the three endpoints that return an invoice detail (open-lock, create, update).
 * Products and linked POs are department-specific and delegated to the matching
 * {@link InvoiceDepartmentDetailResolver}; charges and taxes are transversal to every department.
 */
@Slf4j
@Component
public class InvoiceDetailResolver {

    private final InvoiceMapper mapper;
    private final InvoiceChargeMapper chargeMapper;
    private final InvoiceTaxMapper taxMapper;
    private final IInvoiceChargeRepository chargeRepository;
    private final IInvoiceTaxRepository taxRepository;
    private final IInvoiceClonedRepository clonedRepository;
    private final Map<ConsecutiveDepartment, InvoiceDepartmentDetailResolver> resolversByDepartment;

    // Hand-written on purpose (the only one in the module): Spring injects the resolver List and
    // this constructor indexes it by department, which @RequiredArgsConstructor cannot express.
    public InvoiceDetailResolver(InvoiceMapper mapper, InvoiceChargeMapper chargeMapper, InvoiceTaxMapper taxMapper,
                                  IInvoiceChargeRepository chargeRepository, IInvoiceTaxRepository taxRepository,
                                  IInvoiceClonedRepository clonedRepository,
                                  List<InvoiceDepartmentDetailResolver> departmentResolvers) {
        this.mapper = mapper;
        this.chargeMapper = chargeMapper;
        this.taxMapper = taxMapper;
        this.chargeRepository = chargeRepository;
        this.taxRepository = taxRepository;
        this.clonedRepository = clonedRepository;
        this.resolversByDepartment = departmentResolvers.stream()
                .collect(Collectors.toMap(InvoiceDepartmentDetailResolver::department, Function.identity()));
    }

    public InvoiceDTO resolve(InvoiceEntity invoice) {
        InvoiceDTO dto = mapper.entityToDTO(invoice);
        InvoiceDepartmentDetailResolver departmentResolver = resolversByDepartment.get(invoice.getDepartment());
        if (departmentResolver == null) {
            log.warn("No InvoiceDepartmentDetailResolver registered for department {} (invoice {}); returning empty product/PO lists",
                    invoice.getDepartment(), invoice.getId());
            dto.setProducts(List.of());
            dto.setLinkedPurchaseOrders(List.of());
        } else {
            dto.setProducts(departmentResolver.products(invoice.getId()));
            dto.setLinkedPurchaseOrders(departmentResolver.linkedPurchaseOrders(invoice.getId()));
        }
        dto.setCharges(chargeRepository.findByInvoice_IdOrderByCreatedAt(invoice.getId()).stream()
                .map(chargeMapper::entityToDTO).toList());
        dto.setTaxes(taxRepository.findByInvoice_IdOrderByCreatedAt(invoice.getId()).stream()
                .map(taxMapper::entityToDTO).toList());
        loadClonedInvoices(invoice, dto);
        loadClonedByInvoice(invoice, dto);
        return dto;
    }

    private void loadClonedInvoices(InvoiceEntity entity, InvoiceDTO dto) {
        var children = entity.getClonedInvoices();
        if (children != null && !children.isEmpty()) {
            dto.setClonedInvoices(children.stream()
                    .map(c -> mapper.entityToDTO(c.getClonedInvoice()))
                    .toList());
        }
    }

    private void loadClonedByInvoice(InvoiceEntity entity, InvoiceDTO dto) {
        clonedRepository.fetchByClonedId(entity.getId())
                .ifPresent(cloned -> dto.setClonedByInvoice(mapper.entityToDTO(cloned.getMainInvoice())));
    }
}

package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.partners.clients.services.IClientService;
import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceMoney;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.response.ClientStatementResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.response.ListInvoiceResponse;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceStatementService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAccessGuard;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAgingCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class InvoiceStatementServiceImpl extends UtilServiceAbs implements IInvoiceStatementService {

    private final IInvoiceRepository repository;
    private final InvoiceMapper mapper;
    private final InvoiceAccessGuard accessGuard;
    private final InvoiceAgingCalculator agingCalculator;
    private final IClientService clientService;

    /**
     * Aggregation over invoices that already exist — no new state. Only {@code BILLED} statuses
     * count (guide §11): a draft was never invoiced and a cancelled one represents nothing owed, so
     * neither belongs in a receivables statement.
     */
    @Override
    @Transactional(readOnly = true)
    public ClientStatementResponse statementByClient(UUID clientId) {
        var client = clientService.findClientById(clientId, false);
        var invoices = repository.findAll(accessGuard.scope().and(ofClient(clientId)).and(billed()));

        var totalInvoiced = sum(invoices, InvoiceEntity::getTotalAmount);
        var totalPaid = sum(invoices, InvoiceEntity::getPaidAmount);

        return new ClientStatementResponse(
                client.getId(),
                client.getName(),
                invoices.size(),
                totalInvoiced,
                totalPaid,
                InvoiceMoney.scaled(totalInvoiced.subtract(totalPaid)),
                agingCalculator.aging(invoices),
                overdueOf(invoices)
        );
    }

    private List<ListInvoiceResponse> overdueOf(List<InvoiceEntity> invoices) {
        return invoices.stream()
                .filter(InvoiceEntity::isOverdue)
                .map(mapper::entityToDTO)
                .map(mapper::dtoToListResponse)
                .toList();
    }

    private BigDecimal sum(List<InvoiceEntity> invoices, Function<InvoiceEntity, BigDecimal> field) {
        return InvoiceMoney.scaled(invoices.stream()
                .map(field)
                .map(InvoiceMoney::orZero)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private Specification<InvoiceEntity> ofClient(UUID clientId) {
        return (root, query, builder) -> builder.equal(root.get("client").get("id"), clientId);
    }

    private Specification<InvoiceEntity> billed() {
        return (root, query, builder) -> root.get("status").in(InvoiceStatus.BILLED);
    }
}

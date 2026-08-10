package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.filters.FilterListInvoice;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceMapper;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceQueryService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAccessGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceQueryServiceImpl extends UtilServiceAbs implements IInvoiceQueryService {

    private final IInvoiceRepository repository;
    private final InvoiceMapper mapper;
    private final InvoiceAccessGuard accessGuard;

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDTO> listAll(Pageable pageable, FilterListInvoice filters) {
        Specification<InvoiceEntity> spec = accessGuard.scope()
                .and(filters == null ? Specification.where(null) : filters.filter());
        return repository.findAll(spec, pageable).map(mapper::entityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDTO> listAllOpenByUser(String username) {
        return repository.fetchAllOpenByUsername(username).stream().map(mapper::entityToDTO).toList();
    }
}

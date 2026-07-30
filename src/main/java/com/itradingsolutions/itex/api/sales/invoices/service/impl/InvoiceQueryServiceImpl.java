package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.NotExistInvoiceException;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceQueryServiceImpl extends UtilServiceAbs implements IInvoiceQueryService {

    private final IInvoiceRepository repository;
    private final InvoiceMapper mapper;
    private final InvoiceAccessGuard guard;

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceDTO> listAll(Pageable pageable, FilterListInvoice filters) {
        Specification<InvoiceEntity> spec = guard.scope()
                .and(filters == null ? Specification.where(null) : filters.filter());
        return repository.findAll(spec, pageable).map(mapper::entityToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDTO> listAllOpenByUser(String username) {
        return repository.fetchAllOpenByUsername(username).stream().map(mapper::entityToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDTO findById(UUID id) {
        return mapper.entityToDTO(findEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceEntity findEntityById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotExistInvoiceException(simpleMessage("sales.invoice.not-exist")));
    }
}

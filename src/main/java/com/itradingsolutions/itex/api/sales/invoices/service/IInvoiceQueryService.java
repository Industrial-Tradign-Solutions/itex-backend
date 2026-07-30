package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.filters.FilterListInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IInvoiceQueryService {

    Page<InvoiceDTO> listAll(Pageable pageable, FilterListInvoice filters);

    List<InvoiceDTO> listAllOpenByUser(String username);

    InvoiceDTO findById(UUID id);

    InvoiceEntity findEntityById(UUID id);
}

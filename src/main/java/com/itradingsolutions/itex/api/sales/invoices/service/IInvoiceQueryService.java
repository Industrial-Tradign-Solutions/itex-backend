package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.filters.FilterListInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IInvoiceQueryService {

    Page<InvoiceDTO> listAll(Pageable pageable, FilterListInvoice filters);

    List<InvoiceDTO> listAllOpenByUser(String username);
}

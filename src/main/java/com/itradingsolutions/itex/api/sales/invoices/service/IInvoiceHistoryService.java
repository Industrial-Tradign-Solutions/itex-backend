package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceChargeDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceHistoryDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoicePaymentDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceProductDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceTaxDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;

import java.util.List;
import java.util.UUID;

public interface IInvoiceHistoryService {

    void addHistory(InvoiceHistoryAction action, InvoiceDTO oldDto, InvoiceDTO newDto);
    void addHistoryProduct(InvoiceHistoryAction action, InvoiceProductDTO oldDto, InvoiceProductDTO newDto, UUID invoiceId);
    void addHistoryCharge(InvoiceHistoryAction action, InvoiceChargeDTO oldDto, InvoiceChargeDTO newDto, UUID invoiceId);
    void addHistoryTax(InvoiceHistoryAction action, InvoiceTaxDTO oldDto, InvoiceTaxDTO newDto, UUID invoiceId);
    void addHistoryPayment(InvoiceHistoryAction action, InvoicePaymentDTO oldDto, InvoicePaymentDTO newDto, UUID invoiceId);
    List<InvoiceHistoryDTO> getHistoryById(UUID invoiceId);
}

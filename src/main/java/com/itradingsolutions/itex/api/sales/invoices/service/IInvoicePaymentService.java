package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoicePaymentDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.request.RegisterInvoicePaymentRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.request.VoidInvoicePaymentRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Payments against an issued Invoice. Every write recalculates {@code paid_amount} and the derived
 * status through {@link InvoiceBalanceCalculator} in the same transaction.
 */
public interface IInvoicePaymentService {

    InvoicePaymentDTO register(UUID invoiceId, RegisterInvoicePaymentRequest request, MultipartFile receipt);

    InvoicePaymentDTO voidPayment(UUID invoiceId, UUID paymentId, VoidInvoicePaymentRequest request);

    List<InvoicePaymentDTO> listByInvoice(UUID invoiceId);
}

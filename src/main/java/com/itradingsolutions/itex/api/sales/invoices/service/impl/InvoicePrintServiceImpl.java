package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.jasper.exceptions.NotGenerateReportException;
import com.itradingsolutions.itex.api.common.jasper.model.enums.JasperReport;
import com.itradingsolutions.itex.api.common.jasper.service.JasperService;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;
import com.itradingsolutions.itex.api.common.util.models.enums.Language;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceNumberFormatter;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.reports.InvoiceReportDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceIpProductRepository;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoicePrintService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAccessGuard;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceDetailResolver;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoicePrintServiceImpl extends UtilServiceAbs implements IInvoicePrintService {

    private final IInvoiceRepository repository;
    private final IInvoiceIpProductRepository productRepository;
    private final InvoiceAccessGuard accessGuard;
    private final InvoiceDetailResolver detailResolver;
    private final InvoiceFinder finder;
    private final JasperService jasperService;

    /**
     * Printing is a read: it only asks that the caller be allowed to see the invoice, without
     * demanding ownership or the edit lock — same as the print of QR/Q/PO.
     *
     * <p>A stored PDF is only reused once the invoice left {@code DRAFT}; while it is a draft the
     * content is still changing, so every print rebuilds the document from the current data.</p>
     */
    @Override
    @Transactional
    public byte[] print(UUID id) {
        var invoice = finder.findDetailById(id);
        accessGuard.assertCanAccess(invoice);
        assertPrintable(invoice);

        try {
            if (invoice.getStatus() != InvoiceStatus.DRAFT && invoice.getPdfUrl() != null)
                return jasperService.getPdfBytes(invoice.getPdfUrl());

            var pdfPath = generate(invoice);

            // Only an official document is worth remembering; a draft's PDF is a throwaway preview,
            // so path_pdf staying null keeps its meaning: "this invoice has been issued".
            if (invoice.getStatus() != InvoiceStatus.DRAFT) {
                invoice.setPdfUrl(pdfPath);
                repository.save(invoice);
            }

            return jasperService.getPdfBytes(pdfPath);
        } catch (JRException | IOException ex) {
            throw new NotGenerateReportException(ex);
        }
    }

    /** Only lives inside the issue transaction; a caller without one is a programming error. */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public String generateFinal(InvoiceEntity invoice) {
        assertPrintable(invoice);
        try {
            return generate(invoice);
        } catch (JRException | IOException ex) {
            throw new NotGenerateReportException(ex);
        }
    }

    private String generate(InvoiceEntity invoice) throws JRException, IOException {
        // The detail is resolved once — that is the expensive part (several queries). The report
        // model is rebuilt per fill because its JRBeanCollectionDataSource fields are stateful and
        // arrive exhausted after the page-count pass.
        var dto = detailResolver.resolve(invoice);
        var template = templateFor(invoice);
        int totalPages = jasperService.getTotalPages(template, new InvoiceReportDTO(dto));

        return jasperService.generatePDF(
                template,
                new InvoiceReportDTO(dto),
                InvoiceNumberFormatter.officialOrDraft(invoice.getNumber(), invoice.getDraftNumber()),
                invoice.getCreatedAt(),
                invoice.getDepartment(),
                ConsecutiveModule.INV,
                totalPages
        );
    }

    private void assertPrintable(InvoiceEntity invoice) {
        if (!productRepository.existsByInvoice_Id(invoice.getId()))
            throw new NotGenerateReportException(simpleMessage("sales.invoice.not-generate-doc"));
    }

    private JasperReport templateFor(InvoiceEntity invoice) {
        var language = invoice.getClient() != null ? invoice.getClient().getLanguage() : null;
        return Language.ENGLISH.equals(language) ? JasperReport.IP_INV_EN : JasperReport.IP_INV_ES;
    }
}

package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.common.storage.service.IFileService;
import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceNumberFormatter;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Where a payment receipt lives on disk. Mirrors the layout the generated PDFs already use
 * ({@code {data}/{year}/{month}/{department}/{module}/}), with a {@code payments} leaf so receipts
 * do not mix with the invoice documents themselves.
 *
 * <p>The stored name is suffixed with a random id: unlike {@code uploadTempFile}, two clients
 * uploading {@code receipt.pdf} must not overwrite each other.</p>
 */
@Component
@RequiredArgsConstructor
public class InvoiceReceiptStorage {

    private static final String MODULE_FOLDER = "INV";
    private static final String RECEIPTS_FOLDER = "payments";
    static final String[] VALID_EXTENSIONS = {
            IFileService.FILE_EXT_PDF, IFileService.FILE_EXT_JPG,
            IFileService.FILE_EXT_JPEG, IFileService.FILE_EXT_PNG
    };

    private final IFileService fileService;

    /** Validates the file type and stores it, returning the absolute path to persist. */
    public String store(InvoiceEntity invoice, MultipartFile receipt) {
        var originalName = receipt.getOriginalFilename();
        fileService.validateFileExt(originalName, VALID_EXTENSIONS);
        return fileService.saveDataFile(receipt, folder(invoice), fileName(invoice, originalName));
    }

    private String folder(InvoiceEntity invoice) {
        var createdAt = invoice.getCreatedAt();
        return String.format("%d/%02d/%s/%s/%s",
                createdAt.getYear(), createdAt.getMonthValue(),
                invoice.getDepartment().name(), MODULE_FOLDER, RECEIPTS_FOLDER);
    }

    private String fileName(InvoiceEntity invoice, String originalName) {
        var number = InvoiceNumberFormatter.officialOrDraft(invoice.getNumber(), invoice.getDraftNumber());
        return String.format("%s-%s.%s", number, UUID.randomUUID(), fileService.fileExtension(originalName));
    }
}

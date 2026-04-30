package com.itradingsolutions.itex.api.common.jasper.service;

import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;
import com.itradingsolutions.itex.api.common.jasper.model.enums.JasperReport;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.time.ZonedDateTime;

public interface JasperService {
    String PDF_EXTENSION = ".pdf";

    String generatePDF(JasperReport report, Object model, String pdfNumber, ZonedDateTime createdAt, ConsecutiveDepartment department, ConsecutiveModule module, int totalPages) throws JRException, IOException;
    int getTotalPages(JasperReport report, Object model) throws JRException, IOException;
    byte[] getPdfBytes(String pdfAbsolutePath) throws IOException;
}

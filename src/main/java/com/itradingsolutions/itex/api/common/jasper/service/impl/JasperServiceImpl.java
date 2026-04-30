package com.itradingsolutions.itex.api.common.jasper.service.impl;

import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;
import com.itradingsolutions.itex.api.common.jasper.model.enums.JasperReport;
import com.itradingsolutions.itex.api.common.jasper.service.JasperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JasperServiceImpl implements JasperService {

    @Value("${itex.files.folder.data}")
    private String baseDataPath;

    @Value("${itex.files.folder.jasper}")
    private String baseJasperPath;

    @Override
    public String generatePDF(JasperReport report, Object model, String pdfNumber, ZonedDateTime createdAt, ConsecutiveDepartment department, ConsecutiveModule module, int totalPages) throws JRException, IOException {
        Map<String, Object> params = getParameters();
        params.put("TOTAL_PAGES", totalPages);
        JasperPrint finalPrint = getJasperPrint(model, report, params);
        var pdfPath = getPath(createdAt, department, module, pdfNumber);
        JasperExportManager.exportReportToPdfFile(finalPrint, pdfPath);
        return pdfPath;
    }

    @Override
    public int getTotalPages(JasperReport report, Object model) throws JRException, IOException {
        JasperPrint jasperPrint = getJasperPrint(model, report, getParameters());
        return jasperPrint.getPages().size();
    }

    private JasperPrint getJasperPrint(Object model, JasperReport report, Map<String, Object> params)throws JRException, IOException {
        JRDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(model));
        try (InputStream is = getJasperStream(report)) {
            return JasperFillManager.fillReport(is, params, dataSource);
        }
    }

    @Override
    public byte[] getPdfBytes(String pdfPath) throws IOException {
        var path = Paths.get(pdfPath);
        if (Files.exists(path))
            return Files.readAllBytes(path);
        throw new IOException("File not found: " + pdfPath);
    }

    private String getPath( ZonedDateTime createdAt, ConsecutiveDepartment department, ConsecutiveModule module, String pdfNumber) {
        String year = String.valueOf(createdAt.getYear());
        String month = String.format("%02d", createdAt.getMonthValue());
        var basePath = Paths.get(baseDataPath + year + "/" + month + "/" + department.name() + "/" + module.name() + "/");

        try {
            if (!Files.exists(basePath))
                Files.createDirectories(basePath);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return  basePath + "/" + pdfNumber + JasperService.PDF_EXTENSION;
    }

    private InputStream getJasperStream(JasperReport report) throws FileNotFoundException {
        String jasperPath = baseJasperPath + report.getPath();
        return new FileInputStream(jasperPath);
    }
    private Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();

        InputStream logoStream = getClass().getResourceAsStream("/images/logo.png");
        parameters.put("logo", logoStream);
        parameters.put("name", "INDUSTRIAL TRADING SOLUTIONS CORP");
        parameters.put("address", "1200 Anastasia Ave, Suite 150 - Miami, Fl. 33134");
        parameters.put("phone", "+1 (305) 507-8496");
        parameters.put("email", "info@itradingsolutions.com");
        parameters.put("web", "www.itradingsolutions.com");
        parameters.put("floridaTaxExempt", "23-8015182512-0");
        return parameters;
    }

}

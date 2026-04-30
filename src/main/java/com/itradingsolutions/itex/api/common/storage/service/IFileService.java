package com.itradingsolutions.itex.api.common.storage.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface IFileService {

    String FILE_EXT_JPG = "jpg";
    String FILE_EXT_PNG = "png";
    String FILE_EXT_PDF = "pdf";
    String FILE_EXT_DOC = "doc";
    String FILE_EXT_DOCX = "docx";
    String FILE_EXT_XLS = "xls";
    String FILE_EXT_XLSX = "xlsx";
    String FILE_EXT_PPT = "ppt";
    String FILE_EXT_PPTX = "pptx";
    String FILE_EXT_JPEG = "jpeg";

    void validateFileExt(String fileName, String[] validExtensions);
    Path uploadTempFile(MultipartFile file);
    void deleteTempFile(Path filePath);

    String fileExtension(Path filePath);
}

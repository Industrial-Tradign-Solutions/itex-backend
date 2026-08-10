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

    /**
     * Stores a file permanently under the data folder and returns its absolute path.
     *
     * @param relativeFolder folder inside the data root, e.g. {@code 2026/08/IP/INV/payments};
     *                       created if missing
     * @param fileName       final name including extension; must already be unique, this method
     *                       overwrites a file of the same name
     */
    String saveDataFile(MultipartFile file, String relativeFolder, String fileName);

    String fileExtension(Path filePath);

    /** Extension of an uploaded file, lowercase and without the dot. */
    String fileExtension(String fileName);
}

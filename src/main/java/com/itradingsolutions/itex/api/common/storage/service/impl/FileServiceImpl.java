package com.itradingsolutions.itex.api.common.storage.service.impl;

import com.itradingsolutions.itex.api.common.storage.exceptions.NotValidFileException;
import com.itradingsolutions.itex.api.common.storage.exceptions.SaveFileException;
import com.itradingsolutions.itex.api.common.storage.service.IFileService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class FileServiceImpl extends UtilServiceAbs implements IFileService {

    @Value("${itex.files.folder.data}")
    private String baseDataPath;
    @Value("${itex.files.folder.temp}")
    private String baseTempPath;

    private static final Set<String> VALID_EXTENSIONS = new HashSet<>(Arrays.asList(FILE_EXT_JPG, FILE_EXT_PNG, FILE_EXT_PDF, FILE_EXT_DOC, FILE_EXT_DOCX, FILE_EXT_XLS, FILE_EXT_XLSX, FILE_EXT_PPT, FILE_EXT_PPTX, FILE_EXT_JPEG));
    private static final String MESSAGE_TEMPLATE_FILE_TYPE_ERROR = "file.type.error";

    private void validateFile(String fileName) {
        String fileExtension = getFileExtension(fileName);
        if (!VALID_EXTENSIONS.contains(fileExtension))
            throw new NotValidFileException(compositeMessage(MESSAGE_TEMPLATE_FILE_TYPE_ERROR,  new String[]{fileName, fileExtension}));
    }

    private String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filePath.substring(dotIndex + 1).toLowerCase();
    }

    @Override
    public void validateFileExt(String fileName, String[] validExtensions) {
        validateFile(fileName);
        for(String extension : validExtensions) {
            if (!VALID_EXTENSIONS.contains(extension))
                throw new NotValidFileException(compositeMessage(MESSAGE_TEMPLATE_FILE_TYPE_ERROR,  new String[]{fileName, extension}));
        }
        var fileExt = getFileExtension(fileName);
        if (!Arrays.asList(validExtensions).contains(fileExt))
            throw new NotValidFileException(compositeMessage(MESSAGE_TEMPLATE_FILE_TYPE_ERROR,  new String[]{fileName, fileExt}));
    }

    @Override
    public Path uploadTempFile(MultipartFile file) {
        Path path = Paths.get(baseTempPath).resolve(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            file.transferTo(path);
        } catch (IOException e) {
            throw new SaveFileException(simpleMessage("file.save.error"), e);
        }
        return path;
    }

    @Override
    public void deleteTempFile(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new SaveFileException(simpleMessage("file.delete.error"), e);
        }
    }

    @Override
    public String fileExtension(Path filePath) {
        return getFileExtension(filePath.getFileName().toString());
    }
}

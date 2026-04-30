package com.itradingsolutions.itex.config.files;

import com.itradingsolutions.itex.api.common.jasper.service.JasperService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

@Component
@Slf4j
public class FilesConfig {

    @Value("${itex.files.folder.data}")
    private String baseDataPath;
    @Value("${itex.files.folder.temp}")
    private String baseTempPath;

    @PostConstruct
    public void createDataFolder() throws IOException {
        var dataPath = Paths.get(baseDataPath);
        if (!Files.exists(dataPath))
            log.info("✔  Created Data Folder: {}", Files.createDirectories(dataPath));
    }

    @PostConstruct
    public void createTempFolder() throws IOException {
        var tempPath = Paths.get(baseTempPath);
        if (Files.exists(tempPath))
            deleteDirectory(tempPath);
        log.info("✔  Created Temp Folder: {}", Files.createDirectories(tempPath));
    }

    private void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path)) return;

        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}

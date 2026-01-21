package com.onlineStore.admin.category.controller.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {


    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);

    public static void saveFile(String uploadDir, String filename,
                                MultipartFile multipartFile) throws IOException {

        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {

            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(filename);

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException ex) {

            LOGGER.error("could not save the file" + filename, ex);
            throw new IOException("could not save the file" + filename, ex);
        }


    }


    public static void cleanDir(String dir) throws IOException {

        Path dirPath = Paths.get(dir);

        try {
            Files.list(dirPath).forEach(file -> {
                if (!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                    } catch (IOException ex) {
                        LOGGER.error("File not found: " + file.getFileName());
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Not a valid directory: " + dir);
        }
    }


}


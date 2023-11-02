package com.onlineStore.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUpLoadUtil {

    public static void saveFile(String uploadDir,String filename,
                                MultipartFile multipartFile  ) throws IOException {

        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try(InputStream inputStream = multipartFile.getInputStream() ) {
            Path filePAth = uploadPath.resolve(filename);
            Files.copy(inputStream, filePAth, StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                throw new RuntimeException(   "could not save File" +  filename, e);
            }
        }

    }

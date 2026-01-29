package com.onlineStore.admin.utility.storage;

import com.onlineStore.admin.utility.FileUploadUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Implementation of StorageService using the local file system.
 * Wraps the existing FileUploadUtil logic.
 */
@Service
public class FileSystemStorageService implements StorageService {

    @Override
    public void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    }

    @Override
    public void cleanDir(String dir) throws IOException {
        FileUploadUtil.cleanDir(dir);
    }

    @Override
    public void deleteDir(String dir) throws IOException {
        FileUploadUtil.deleteDir(dir);
    }

    @Override
    public String getStoragePath(Object entityId, String type) {
        return FileUploadUtil.getStoragePath(entityId, type);
    }
}

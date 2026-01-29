package com.onlineStore.admin.utility.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Interface for file storage operations.
 * Decouples business logic from file system specifics.
 */
public interface StorageService {

    /**
     * Save a file to the storage system.
     *
     * @param uploadDir     Relative path or key for the directory.
     * @param fileName      The name of the file.
     * @param multipartFile The file content.
     * @throws IOException If storage fails.
     */
    void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException;

    /**
     * Clean a directory (remove non-directory files).
     *
     * @param dir Directory path/key.
     * @throws IOException If cleanup fails.
     */
    void cleanDir(String dir) throws IOException;

    /**
     * Delete a directory and its contents.
     *
     * @param dir Directory path/key.
     * @throws IOException If deletion fails.
     */
    void deleteDir(String dir) throws IOException;

    /**
     * Construct a storage path for a given entity.
     *
     * @param entityId The ID of the entity.
     * @param type     The type of asset (e.g., "products", "users").
     * @return The resolved path string.
     */
    String getStoragePath(Object entityId, String type);
}

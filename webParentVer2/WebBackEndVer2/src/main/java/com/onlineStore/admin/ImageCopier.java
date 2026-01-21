package com.onlineStore.admin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ImageCopier {

    String sourceFolder = "C:\\Users\\YourName\\Pictures\\A"; // فولدر الصور الأصلية
    String destinationFolder = "C:\\Users\\YourName\\Pictures\\B"; // فولدر النسخ

    public static void imageCopier(String sourceFolder , String destinationFolder ) {
        // 🟡 غيّر المسارات حسب جهازك


        File sourceDir = new File(sourceFolder);
        File[] files = sourceDir.listFiles((dir, name) -> name.matches(".*\\.(jpg|jpeg|png|gif|bmp)$"));

        if (files == null) {
            System.out.println("❌ Source folder not found or empty");
            return;
        }

        for (File file : files) {
            Path destinationPath = Path.of(destinationFolder, file.getName());

            try {
                Files.copy(file.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("✅ Copied: " + file.getName());
            } catch (IOException e) {
                System.err.println("⚠️ Failed to copy " + file.getName() + ": " + e.getMessage());
            }
        }
    }
}
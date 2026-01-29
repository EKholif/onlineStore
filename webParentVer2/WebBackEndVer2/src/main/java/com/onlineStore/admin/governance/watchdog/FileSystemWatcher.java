package com.onlineStore.admin.governance.watchdog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Monitors the file system for architectural violations using Java NIO WatchService.
 * Specifically checks for the creation of legacy folders (e.g., user-photos) in the root directory.
 */
public class FileSystemWatcher implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemWatcher.class);
    // Legacy folder patterns that are strictly prohibited at the root level
    private static final List<String> PROHIBITED_ROOT_FOLDERS = List.of(
            "user-photos",
            "category-images",
            "brand-logos",
            "product-images",
            "site-logo",
            "customer-photos"
    );
    private final List<Path> directoriesToWatch;
    private final ViolationRegistry violationRegistry;
    private volatile boolean running = true;

    public FileSystemWatcher(List<Path> directoriesToWatch, ViolationRegistry violationRegistry) {
        this.directoriesToWatch = directoriesToWatch;
        this.violationRegistry = violationRegistry;
    }

    @Override
    public void run() {
        LOGGER.info("Starting Architecture Watchdog File System Monitor...");

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Map<WatchKey, Path> keys = new HashMap<>();

            for (Path dir : directoriesToWatch) {
                if (Files.exists(dir) && Files.isDirectory(dir)) {
                    WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
                    keys.put(key, dir);
                    LOGGER.info("Watching directory for architectural violations: {}", dir.toAbsolutePath());
                } else {
                    LOGGER.warn("Directory does not exist or is not a directory, skipping watch: {}", dir);
                }
            }

            while (running) {
                WatchKey key;
                try {
                    // Poll with timeout to allow checking 'running' flag periodically
                    key = watchService.poll(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                if (key == null) {
                    continue;
                }

                Path dir = keys.get(key);
                if (dir == null) {
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    // Context for directory entry event is the file name of entry
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                    Path fullPath = dir.resolve(fileName);

                    checkViolation(fullPath, fileName.toString());
                }

                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);
                    if (keys.isEmpty()) {
                        LOGGER.warn("All watched directories are inaccessible. Stopping watcher.");
                        break;
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.error("Fatal error in Architecture Watchdog FileSystemWatcher", e);
        }

        LOGGER.info("Architecture Watchdog File System Monitor Stopped.");
    }

    private void checkViolation(Path fullPath, String fileName) {
        // Check if the created item is a directory (or even a file) that matches prohibited patterns
        // Note: For robustness, we flag both files and dirs with these names, though usually they are dirs.

        boolean isLegacy = PROHIBITED_ROOT_FOLDERS.stream().anyMatch(fileName::contains);

        if (isLegacy) {
            String msg = "VIOLATION DETECTED: Legacy folder structure created: '" + fileName + "'. " +
                    "All assets must be stored in 'tenants/{id}/assets'.";
            LOGGER.error(msg);

            violationRegistry.addViolation(
                    "LEGACY_STRUCTURE_VIOLATION",
                    fullPath.toAbsolutePath().toString(),
                    msg
            );
        }
    }

    public void stop() {
        this.running = false;
    }
}

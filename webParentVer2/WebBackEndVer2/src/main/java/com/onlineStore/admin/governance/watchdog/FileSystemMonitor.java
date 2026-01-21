package com.onlineStore.admin.governance.watchdog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Runnable task that monitors registered directories for changes.
 * Specifically looks for the creation of prohibited "legacy" folders.
 */
public class FileSystemMonitor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemMonitor.class);
    // Legacy folder names that are strictly prohibited in the root or webParent
    private static final Set<String> PROHIBITED_ROOT_DIRS = Set.of(
            "user-photos",
            "site-logo",
            "products-photos",
            "categories-photos",
            "brands-photos",
            "common-assets");
    private final WatchService watchService;
    private final Map<WatchKey, Path> keys;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ViolationRegistry violationRegistry;

    public FileSystemMonitor(ViolationRegistry violationRegistry) {
        this.violationRegistry = violationRegistry;
        this.keys = new HashMap<>();
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to request FileSystem WatchService", e);
        }
    }

    public void registerPath(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            LOGGER.warn("Watchdog: Directory {} does not exist, skipping registration.", dir);
            return;
        }

        // We only care about creation of new directories/files
        WatchKey key = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        keys.put(key, dir);
        LOGGER.info("Watchdog: Registered monitor for {}", dir);
    }

    public void stop() {
        running.set(false);
        try {
            watchService.close();
        } catch (IOException e) {
            LOGGER.error("Error closing watcher", e);
        }
    }

    @Override
    public void run() {
        running.set(true);
        LOGGER.info("Watchdog: FileSystemMonitor thread started.");

        while (running.get()) {
            WatchKey key;
            try {
                // Poll with timeout to allow checking 'running' flag periodically
                key = watchService.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException x) {
                return;
            } catch (ClosedWatchServiceException e) {
                return;
            }

            if (key == null) {
                continue;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                LOGGER.warn("WatchKey not recognized!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();
                Path child = dir.resolve(filename);

                handleEvent(dir, child, kind);
            }

            // Reset key to receive further events
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    private void handleEvent(Path parent, Path child, WatchEvent.Kind<?> kind) {
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
            String folderName = child.getFileName().toString();

            // 1. Check for Legacy Folders in Root/WebParent
            // We assume 'parent' is one of the monitored roots
            if (PROHIBITED_ROOT_DIRS.contains(folderName)) {
                reportViolation("LEGACY_PATH_CREATION",
                        "Prohibited legacy folder created: " + child.toAbsolutePath(),
                        "ALL_ASSETS_MUST_BE_TENANT_ISOLATED");
            }

            // 2. Check for loose files in tenants/ root (if we are watching tenants/)
            // Ideally tenants/ should only contain directories (tenantIDs)
            if (parent.getFileName().toString().equals("tenants") && !Files.isDirectory(child)) {
                // It's a file directly inside tenants/, typically not allowed
                reportViolation("INVALID_TENANT_STRUCTURE",
                        "File created directly in tenants root: " + child.toAbsolutePath(),
                        "TENANT_ROOT_ONLY_DIRECTORIES");
            }
        }
    }

    private void reportViolation(String code, String message, String rule) {
        LOGGER.error("🚨 ARCHITECTURE VIOLATION [{}] {}: Rule={}", code, message, rule);
        System.err.println("🚨 [WATCHDOG] VIOLATION: " + message);
        if (violationRegistry != null) {
            violationRegistry.recordViolation(code, message, rule);
        }

        // TODO: Persist to runtime_violation_report.md
    }
}

package com.onlineStore.admin.governance.watchdog;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for the Architecture Watchdog.
 * Verifies that creating prohibited folders triggers a violation.
 */
public class ArchitectureWatchdogTest {

    private Path tempRoot;
    private ViolationRegistry registry;
    private FileSystemWatcher watcher;
    private Thread watcherThread;

    @BeforeEach
    public void setup() throws IOException {
        // Create a temporary root directory to watch
        tempRoot = Files.createTempDirectory("watchdog-test-root");
        registry = new ViolationRegistry();

        // Initialize watcher
        watcher = new FileSystemWatcher(Collections.singletonList(tempRoot), registry);

        // Start watcher in a separate thread
        watcherThread = new Thread(watcher);
        watcherThread.start();

        // Give the watcher a moment to initialize and register keys
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Stop watcher
        if (watcher != null) {
            watcher.stop();
        }
        if (watcherThread != null) {
            watcherThread.interrupt();
        }

        // Clean up temp directory
        FileSystemUtils.deleteRecursively(tempRoot);
    }

    @Test
    public void testDetectsLegacyFolderCreation() throws IOException, InterruptedException {
        // 1. Create a prohibited folder
        Path legacyFolder = tempRoot.resolve("site-logo");
        Files.createDirectories(legacyFolder);

        // 2. Wait for the WatchService to poll and process
        // The watcher polls every 1 second, so we wait slightly longer
        Thread.sleep(2000);

        // 3. Verify violation
        List<ViolationRegistry.Violation> violations = registry.getViolations();
        assertEquals(1, violations.size(), "Should detect exactly one violation");

        ViolationRegistry.Violation v = violations.get(0);
        assertTrue(v.getDescription().contains("Legacy folder structure created"), "Description should match");
        assertTrue(v.getPath().contains("site-logo"), "Path should contain the culprit");
    }

    @Test
    public void testIgnoresValidFolderCreation() throws IOException, InterruptedException {
        // 1. Create a valid folder
        Path validFolder = tempRoot.resolve("tenants");
        Files.createDirectories(validFolder);

        // 2. Wait
        Thread.sleep(2000);

        // 3. Verify NO violation
        List<ViolationRegistry.Violation> violations = registry.getViolations();
        assertTrue(violations.isEmpty(), "Should NOT detect violation for valid folder");
    }
}

package com.onlineStore.admin.governance.watchdog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ArchitectureWatchdogTest {

    @TempDir
    Path tempDir;
    private ArchitectureWatchdogService watchdogService;
    private ViolationRegistry violationRegistry;
    private FileSystemMonitor monitor; // We access this indirectly via registry or by exposing it for test?

    @BeforeEach
    public void setup() throws IOException {
        violationRegistry = new ViolationRegistry();
        // creating manual monitor for test to avoid threading complexity of the full
        // service if possible of full service
        // But better test the service.

        watchdogService = new ArchitectureWatchdogService(violationRegistry);

        // We need to inject the temp directory as a root to monitor.
        // ArchitectureWatchdogService hardcodes paths currently.
        // We should really refactor ArchitectureWatchdogService to allow adding paths,
        // or use reflection/subclass for testing.
        // For this test, I'll instantiate FileSystemMonitor directly.
    }

    @Test
    public void testLegacyFolderCreationDetection() throws IOException, InterruptedException {
        // Given
        FileSystemMonitor testMonitor = new FileSystemMonitor(violationRegistry);
        testMonitor.registerPath(tempDir);

        Thread monitorThread = new Thread(testMonitor);
        monitorThread.start();

        // Allow monitor to start
        Thread.sleep(500);

        // When: Create a prohibited folder
        Path prohibited = tempDir.resolve("site-logo");
        Files.createDirectories(prohibited);

        // And: Create a valid folder (should not trigger)
        Files.createDirectories(tempDir.resolve("valid-folder"));

        // Allow file events to process
        Thread.sleep(2000);

        // Then
        List<ViolationRegistry.Violation> violations = violationRegistry.getViolations();

        // Using stream to find specific violation
        boolean found = violations.stream()
                .anyMatch(v -> v.message.contains("site-logo") && v.code.equals("LEGACY_PATH_CREATION"));

        testMonitor.stop();

        if (!found) {
            System.out.println("Violations found: ");
            violations.forEach(v -> System.out.println(v.message));
        }

        Assertions.assertTrue(found, "Should have detected creation of 'site-logo'");
    }
}

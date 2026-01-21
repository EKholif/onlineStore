package com.onlineStore.admin.governance.watchdog;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service that orchestrates the runtime architectural compliance checks.
 * It manages the FileSystemMonitor thread and integrates with the
 * SystemArchitectureAuditor.
 */
@Service
public class ArchitectureWatchdogService {

    private final FileSystemMonitor fileSystemMonitor;
    private final ExecutorService executorService;
    private final ViolationRegistry violationRegistry;

    public ArchitectureWatchdogService(ViolationRegistry violationRegistry) {
        this.violationRegistry = violationRegistry;
        this.fileSystemMonitor = new FileSystemMonitor(violationRegistry);
        this.executorService = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "Architecture-Watchdog-Thread");
            t.setDaemon(true); // Ensure it doesn't block app shutdown
            return t;
        });
    }

    @PostConstruct
    public void startWatchdog() {
        System.out.println("🐕 Architecture Watchdog starting...");

        // Define root paths to monitor
        // Using "tenants" as a critical directory to watch for creation of new assets
        // Using "." (root) to watch for creation of prohibited folders like "site-logo"
        // in root

        // Note: Watching root "." might be noisy, we need to filter carefully in the
        // Monitor.
        // Better to watch specific parents if possible, but for "no root assets" we
        // must watch root.

        try {
            fileSystemMonitor.registerPath(Paths.get("tenants"));
            fileSystemMonitor.registerPath(Paths.get(".")); // Watch root for creation of legacy folders

            executorService.submit(fileSystemMonitor);

            System.out.println("✅ Architecture Watchdog is active and monitoring.");
        } catch (IOException e) {
            System.err.println("❌ Failed to start Architecture Watchdog file monitor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stopWatchdog() {
        System.out.println("🛑 Stopping Architecture Watchdog...");
        fileSystemMonitor.stop();
        executorService.shutdownNow();
    }
}

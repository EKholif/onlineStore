package com.onlineStore.admin.governance.watchdog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service responsible for starting and stopping the Architecture Watchdog file
 * system monitor.
 * It identifies the root directories to watch (e.g., project root, tenants
 * folder).
 */
@Service
public class ArchitectureWatchdogService implements SmartInitializingSingleton, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArchitectureWatchdogService.class);

    private final ViolationRegistry violationRegistry;
    private FileSystemWatcher fileSystemWatcher;
    private ExecutorService executorService;

    public ArchitectureWatchdogService(ViolationRegistry violationRegistry) {
        this.violationRegistry = violationRegistry;
    }

    @Override
    public void afterSingletonsInstantiated() {
        LOGGER.info("Initializing Architecture Watchdog Service...");

        List<Path> watchTargets = new ArrayList<>();

        // Strategy: Watch the current working directory (project root)
        // This is where users might accidentally create "user-photos" etc.
        Path rootPath = Paths.get(".").toAbsolutePath().normalize();
        watchTargets.add(rootPath);

        // Also watch webParent/WebBackEnd if possible, though root covers most
        // 'accidental' creations
        // We can add more specific paths if needed.

        LOGGER.info("Watchdog targets: {}", watchTargets);

        this.fileSystemWatcher = new FileSystemWatcher(watchTargets, violationRegistry);

        this.executorService = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "ArchWatchdog-Monitor");
            t.setDaemon(true);
            return t;
        });

        this.executorService.submit(fileSystemWatcher);
        LOGGER.info("Architecture Watchdog Service started successfully.");
    }

    @Override
    public void destroy() {
        if (fileSystemWatcher != null) {
            fileSystemWatcher.stop();
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }
        LOGGER.info("Architecture Watchdog Service stopped.");
    }
}

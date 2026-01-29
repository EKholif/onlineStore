package com.onlineStore.admin.governance.watchdog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Periodically reports architecture violations to a file.
 */
@Component
public class WatchdogReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WatchdogReporter.class);
    // Report file path relative to project root
    private static final String REPORT_FILE = "project-reports/runtime_violation_report.md";
    private final ViolationRegistry violationRegistry;

    public WatchdogReporter(ViolationRegistry violationRegistry) {
        this.violationRegistry = violationRegistry;
    }

    // Run every 1 minute (fixedRate = 60000ms)
    @Scheduled(fixedRate = 60000)
    public void generateReport() {
        List<ViolationRegistry.Violation> violations = violationRegistry.getViolations();
        if (violations.isEmpty()) {
            return; // No news is good news
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REPORT_FILE))) {
            writer.write("# Architecture Watchdog Runtime Report\n");
            writer.write("Generated at: " + LocalDateTime.now() + "\n\n");

            writer.write("| Timestamp | Type | Path | Description |\n");
            writer.write("|---|---|---|---|\n");

            for (ViolationRegistry.Violation v : violations) {
                writer.write(String.format("| %s | %s | %s | %s |\n",
                        v.getTimestamp(),
                        v.getType(),
                        v.getPath(),
                        v.getDescription()));
            }

            LOGGER.debug("Watchdog report updated with {} violations.", violations.size());

        } catch (IOException e) {
            LOGGER.error("Failed to write Architecture Watchdog report", e);
        }
    }
}

package com.onlineStore.admin.governance.watchdog;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry to store and report architectural violations detected at runtime.
 */
@Component
public class ViolationRegistry {

    // Assumes execution from webParent/WebBackEnd.
    // .. (WebBackEnd) -> .. (webParent) -> .. (onlineStore) -> project-reports
    private static final Path REPORT_DIR = Paths.get("../../project-reports");
    private static final Path REPORT_PATH = REPORT_DIR.resolve("runtime_violation_report.md");
    private static final Path HTML_REPORT_PATH = REPORT_DIR.resolve("watchdog_status_report.html");
    private final List<Violation> violations = new CopyOnWriteArrayList<>();

    public ViolationRegistry() {
        try {
            if (!Files.exists(REPORT_DIR)) {
                Files.createDirectories(REPORT_DIR);
            }
        } catch (IOException e) {
            System.err.println("Failed to create report directory: " + e.getMessage());
        }
    }

    public void recordViolation(String code, String message, String rule) {
        Violation v = new Violation(code, message, rule, LocalDateTime.now());
        violations.add(v);

        // Append to report immediately (or we could batch)
        appendViolationToReport(v);
    }

    public List<Violation> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    private void appendViolationToReport(Violation v) {
        String entry = String.format("| %s | %s | %s | %s |\n",
                v.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                v.code,
                v.message,
                v.rule);

        try {
            if (!Files.exists(REPORT_PATH)) {
                String header = "# Runtime Architectural Violations Report\n\n" +
                        "| Timestamp | Code | Message | Rule |\n" +
                        "|---|---|---|---|\n";
                Files.writeString(REPORT_PATH, header, StandardOpenOption.CREATE);
            }
            Files.writeString(REPORT_PATH, entry, StandardOpenOption.APPEND);
            generateHtmlReport();
        } catch (IOException e) {
            System.err.println("Failed to write to violation report: " + e.getMessage());
        }
    }

    private void generateHtmlReport() {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
                .append("body { font-family: sans-serif; padding: 20px; }")
                .append("table { border-collapse: collapse; width: 100%; }")
                .append("th, td { border: 1px solid #ddd; padding: 8px; }")
                .append("th { background-color: #f2f2f2; }")
                .append(".violation { color: red; }")
                .append("</style></head><body>")
                .append("<h1>🐶 Architecture Watchdog Status</h1>")
                .append("<p>Last Updated: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .append("</p>")
                .append("<h2>Violations Detected: ").append(violations.size()).append("</h2>")
                .append("<table><thead><tr><th>Time</th><th>Code</th><th>Message</th><th>Rule</th></tr></thead><tbody>");

        for (Violation v : violations) {
            html.append("<tr>")
                    .append("<td>").append(v.timestamp.format(DateTimeFormatter.ISO_LOCAL_TIME)).append("</td>")
                    .append("<td>").append(v.code).append("</td>")
                    .append("<td class='violation'>").append(v.message).append("</td>")
                    .append("<td>").append(v.rule).append("</td>")
                    .append("</tr>");
        }

        html.append("</tbody></table></body></html>");

        try {
            Files.writeString(HTML_REPORT_PATH, html.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Violation {
        public final String code;
        public final String message;
        public final String rule;
        public final LocalDateTime timestamp;

        public Violation(String code, String message, String rule, LocalDateTime timestamp) {
            this.code = code;
            this.message = message;
            this.rule = rule;
            this.timestamp = timestamp;
        }
    }
}

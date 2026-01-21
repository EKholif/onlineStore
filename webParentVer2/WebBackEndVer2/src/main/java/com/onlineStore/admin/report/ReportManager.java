package com.onlineStore.admin.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlineStoreCom.tenant.TenantContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AG-REPORT-SYS-001: Centralized, Tenant-Aware Report Manager.
 * Handles generation, storage, and retrieval of reports in a strict folder
 * structure.
 */
@Service
public class ReportManager {

    private static final String ROOT_DIR = "tenants";
    private static final String REPORT_DIR = "reports";

    /**
     * Generates a report and saves it to the tenant's report folder.
     *
     * @param type The type of report.
     * @param data The data object to serialize (usually a List or DTO).
     * @param <T>  Type of the data.
     * @return The path to the generated file.
     * @throws IOException If file writing fails.
     */
    public <T> String generateReport(ReportType type, T data) throws IOException {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant Context is missing! Cannot generate report.");
        }

        // AG-TENANT-ISO-002: Strict folder isolation: tenants/{id}/reports/{type}/
        Path tenantReportDir = Paths.get(ROOT_DIR, String.valueOf(tenantId), REPORT_DIR, type.name().toLowerCase());

        if (!Files.exists(tenantReportDir)) {
            Files.createDirectories(tenantReportDir);
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String baseFilename = type.name() + "_REPORT_" + timestamp;

        // 1. Save JSON (Raw Data)
        Path jsonPath = tenantReportDir.resolve(baseFilename + ".json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonPath.toFile(), data);

        // 2. Save HTML (Visual Report)
        Path htmlPath = tenantReportDir.resolve(baseFilename + ".html");
        String htmlContent = generateHtmlContent(type, data, tenantId, timestamp);
        Files.write(htmlPath, htmlContent.getBytes());

        System.out.println("AG-INFO: Generated Reports: " + baseFilename);
        return baseFilename;
    }

    private <T> String generateHtmlContent(ReportType type, T data, Long tenantId, String timestamp) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>").append(type).append(" Report</title>");
        sb.append(
                "<style>body{font-family:sans-serif; padding:20px;} .header{background:#f4f4f4; padding:10px; margin-bottom:20px;} pre{background:#eee; padding:10px;}</style>");
        sb.append("</head><body>");
        sb.append("<div class='header'>");
        sb.append("<h1>").append(type).append(" Report</h1>");
        sb.append("<p><strong>Tenant ID:</strong> ").append(tenantId).append("</p>");
        sb.append("<p><strong>Generated:</strong> ").append(timestamp).append("</p>");
        sb.append("</div>");

        sb.append("<h2>Report Data</h2>");
        sb.append("<pre>").append(data.toString()).append("</pre>"); // Simple string dump for now, can be enhanced with
        // Jackson to HTML table later

        sb.append("</body></html>");
        return sb.toString();
    }

    /**
     * Lists all reports of a specific type for the current tenant.
     */
    public List<String> listReports(ReportType type) throws IOException {
        Long tenantId = TenantContext.getTenantId();
        Path reportDir = Paths.get(ROOT_DIR, String.valueOf(tenantId), REPORT_DIR, type.name().toLowerCase());

        if (!Files.exists(reportDir)) {
            return new ArrayList<>();
        }

        try (Stream<Path> stream = Files.list(reportDir)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }

    public File getReportFile(ReportType type, String filename) {
        Long tenantId = TenantContext.getTenantId();
        // Security check: filename should not contain ".."
        if (filename.contains("..")) {
            throw new SecurityException("Invalid filename");
        }
        return Paths.get(ROOT_DIR, String.valueOf(tenantId), REPORT_DIR, type.name().toLowerCase(), filename).toFile();
    }

    public enum ReportType {
        SALES,
        CUSTOMERS,
        INVENTORY,
        ACTIVITY
    }
}

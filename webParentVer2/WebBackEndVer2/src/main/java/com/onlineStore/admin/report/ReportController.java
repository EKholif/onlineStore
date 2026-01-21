package com.onlineStore.admin.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing Tenant Reports.
 * Provides APIs for generating and retrieving report files.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportManager reportManager;

    @GetMapping("/{type}")
    public ResponseEntity<List<String>> listReports(@PathVariable("type") String typeStr) {
        try {
            ReportManager.ReportType type = ReportManager.ReportType.valueOf(typeStr.toUpperCase());
            List<String> files = reportManager.listReports(type);
            return ResponseEntity.ok(files);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{type}/generate")
    public ResponseEntity<String> generateReport(@PathVariable("type") String typeStr,
                                                 @RequestBody(required = false) Map<String, Object> payload) {
        try {
            // Note: In a real scenario, 'payload' might be replaced by a service call to
            // fetch actual data
            // For now, checks infrastructure
            ReportManager.ReportType type = ReportManager.ReportType.valueOf(typeStr.toUpperCase());

            // Placeholder data if payload is empty, just to demonstrate file creation
            Object data = (payload != null) ? payload : "Scan completed for " + typeStr;

            String filename = reportManager.generateReport(type, data);
            return ResponseEntity.ok("Report generated: " + filename);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to generate report");
        }
    }

    @GetMapping("/{type}/download/{filename}")
    public ResponseEntity<Resource> downloadReport(@PathVariable("type") String typeStr,
                                                   @PathVariable("filename") String filename) {
        try {
            ReportManager.ReportType type = ReportManager.ReportType.valueOf(typeStr.toUpperCase());
            File file = reportManager.getReportFile(type, filename);

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(resource);

        } catch (IllegalArgumentException | SecurityException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

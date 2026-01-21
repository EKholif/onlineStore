package com.onlineStore.admin.knowledge;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * AG-AUDIT-001: Automatic System Architecture Auditor
 * Runs on startup to verify system compliance and generate reports.
 */
@Component
public class SystemArchitectureAuditor {

    private static final String REPORT_BASE_DIR = "reports/system";
    private static final String HTML_REPORT_NAME = "system_audit_latest.html";
    private static final String MARKDOWN_REPORT_PREFIX = "system_audit_";

    // Enforcement Rules
    private static final Set<String> FORBIDDEN_ROOT_DIRS = new HashSet<>(Arrays.asList(
            "site-logo", "user-photos", "brands-photos", "categories-photos",
            "products-photos", "services-photos", "customers-photos", "customer-photos"));

    private static final Set<String> ALLOWED_TENANT_ROOTS = new HashSet<>(Arrays.asList("assets"));
    private static final Set<String> FORBIDDEN_TENANT_SUFFIXES = new HashSet<>(Arrays.asList("-photos"));

    public void runAudit() {
        System.out.println("🛡️ Autonomous Architecture & Runtime Auditor [READ-ONLY MODE] scanning...");

        Path rootPath = detectRootPath();
        AuditContext context = scanRuntime(rootPath);

        generateHtmlReport(rootPath, context);
        generateMarkdownReport(rootPath, context);

        logFinalStatus(context);
    }

    private Path detectRootPath() {
        Path root = Paths.get(".");
        if (Files.exists(Paths.get("webParent", "WebBackEnd"))) {
            return Paths.get("webParent", "WebBackEnd");
        }
        return root;
    }

    private AuditContext scanRuntime(Path root) {
        AuditContext context = new AuditContext();
        context.scanTime = LocalDateTime.now();

        // 1. Scan Root
        try (Stream<Path> stream = Files.list(root)) {
            stream.filter(Files::isDirectory).forEach(path -> {
                String name = path.getFileName().toString();
                if (FORBIDDEN_ROOT_DIRS.contains(name)) {
                    context.addViolation("ROOT", "Legacy folder found: " + name, "Remove manually");
                }
            });
        } catch (IOException e) {
            context.addError("Root scan failed: " + e.getMessage());
        }

        // 2. Scan Tenants
        Path tenantsPath = root.resolve("tenants");
        if (Files.exists(tenantsPath)) {
            try (Stream<Path> stream = Files.list(tenantsPath)) {
                stream.filter(Files::isDirectory).forEach(tenantPath -> {
                    String tenantId = tenantPath.getFileName().toString();
                    context.tenantsScanned++;

                    try (Stream<Path> subStream = Files.list(tenantPath)) {
                        subStream.forEach(sub -> {
                            String subName = sub.getFileName().toString();

                            // Check explicit forbidden folders
                            if (Files.isDirectory(sub)) {
                                if (FORBIDDEN_TENANT_SUFFIXES.stream().anyMatch(subName::endsWith)) {
                                    context.addViolation("TENANT " + tenantId,
                                            "Legacy asset folder detected: " + subName,
                                            "Move to assets/" + subName.replace("-photos", ""));
                                }
                                if (!ALLOWED_TENANT_ROOTS.contains(subName) && !subName.startsWith(".")
                                        && !subName.equals("assets")) {
                                    // Just a warning for unknown folders which aren't strictly 'assets'
                                    context.addWarning("TENANT " + tenantId, "Unknown folder: " + subName,
                                            "Verify purpose");
                                }
                            }

                            // Check Corrupt Artifacts (Directories masquerading as files)
                            if (Files.isDirectory(sub) && (subName.endsWith(".png") || subName.endsWith(".jpg"))) {
                                context.addViolation("TENANT " + tenantId, "Corrupt directory artifact: " + subName,
                                        "Delete directory");
                            }
                        });
                    } catch (IOException e) {
                        context.addError("Tenant scan failed " + tenantId + ": " + e.getMessage());
                    }
                });
            } catch (IOException e) {
                context.addError("Tenants dir scan failed: " + e.getMessage());
            }
        }

        return context;
    }

    private void generateHtmlReport(Path root, AuditContext context) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>System Audit</title>");
        html.append("<style>");
        html.append("body { font-family: system-ui; padding: 20px; background: #f4f6f8; }");
        html.append(
                ".card { background: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
        html.append(".violation { border-left: 4px solid #dc3545; }");
        html.append(".warning { border-left: 4px solid #ffc107; }");
        html.append(".success { border-left: 4px solid #28a745; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 10px; }");
        html.append("th, td { padding: 12px; border-bottom: 1px solid #eee; text-align: left; }");
        html.append("th { background: #f8f9fa; }");
        html.append("</style></head><body>");

        // Executive Summary
        String statusClass = context.violations.isEmpty() ? (context.warnings.isEmpty() ? "success" : "warning")
                : "violation";
        String statusText = context.violations.isEmpty() ? (context.warnings.isEmpty() ? "COMPLIANT" : "WARNING")
                : "VIOLATION";

        html.append("<div class='card ").append(statusClass).append("'>");
        html.append("<h1>System Audit: ").append(statusText).append("</h1>");
        html.append("<p><strong>Scan Time:</strong> ").append(context.scanTime).append("</p>");
        html.append("<p><strong>Tenants Scanned:</strong> ").append(context.tenantsScanned).append("</p>");
        html.append("</div>");

        // Issues
        if (!context.violations.isEmpty() || !context.warnings.isEmpty()) {
            html.append("<div class='card'>");
            html.append("<h2>Detected Issues</h2>");
            html.append("<table><tr><th>Type</th><th>Scope</th><th>Issue</th><th>Recommendation</th></tr>");

            for (Issue i : context.violations) {
                html.append("<tr><td><span style='color:#dc3545'>VIOLATION</span></td><td>")
                        .append(i.scope).append("</td><td>").append(i.message).append("</td><td>")
                        .append(i.recommendation).append("</td></tr>");
            }
            for (Issue i : context.warnings) {
                html.append("<tr><td><span style='color:#ffc107'>WARNING</span></td><td>")
                        .append(i.scope).append("</td><td>").append(i.message).append("</td><td>")
                        .append(i.recommendation).append("</td></tr>");
            }
            html.append("</table></div>");
        } else {
            html.append(
                    "<div class='card success'><h2>✅ No Issues Detected</h2><p>The system is architecturally compliant.</p></div>");
        }

        html.append("</body></html>");
        writeReport(root, HTML_REPORT_NAME, html.toString());
    }

    private void generateMarkdownReport(Path root, AuditContext context) {
        StringBuilder md = new StringBuilder();
        md.append("# System Architecture Audit Report\n\n");
        md.append("**Execution Time:** ").append(context.scanTime).append("\n");
        md.append("**Status:** ").append(context.getOverallStatus()).append("\n\n");

        md.append("## 1. Audit Log\n");
        if (context.violations.isEmpty() && context.warnings.isEmpty()) {
            md.append("No issues found. System is compliant.\n");
        } else {
            for (Issue i : context.violations) {
                md.append("- ❌ **VIOLATION** [").append(i.scope).append("]: ").append(i.message).append("\n");
                md.append("  - *Action:* ").append(i.recommendation).append("\n");
            }
            for (Issue i : context.warnings) {
                md.append("- ⚠️ **WARNING** [").append(i.scope).append("]: ").append(i.message).append("\n");
                md.append("  - *Action:* ").append(i.recommendation).append("\n");
            }
        }

        String filename = MARKDOWN_REPORT_PREFIX + context.scanTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + ".md";
        writeReport(root, filename, md.toString());
    }

    private void writeReport(Path root, String filename, String content) {
        try {
            Path dir = root.resolve(REPORT_BASE_DIR);
            Files.createDirectories(dir);
            Path file = dir.resolve(filename);
            Files.writeString(file, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Audit Report Write Failed: " + e.getMessage());
        }
    }

    private void logFinalStatus(AuditContext context) {
        String status = context.getOverallStatus();
        System.out.println("System Audit Completed: " + status);
    }

    private static class Issue {
        String scope, message, recommendation;

        Issue(String s, String m, String r) {
            scope = s;
            message = m;
            recommendation = r;
        }
    }

    private static class AuditContext {
        LocalDateTime scanTime;
        int tenantsScanned = 0;
        List<Issue> violations = new ArrayList<>();
        List<Issue> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        void addViolation(String scope, String msg, String rec) {
            violations.add(new Issue(scope, msg, rec));
        }

        void addWarning(String scope, String msg, String rec) {
            warnings.add(new Issue(scope, msg, rec));
        }

        void addError(String msg) {
            errors.add(msg);
        }

        String getOverallStatus() {
            if (!violations.isEmpty())
                return "VIOLATIONS";
            if (!warnings.isEmpty())
                return "WARNINGS";
            return "COMPLIANT";
        }
    }
}

package com.onlineStore.admin.governance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Anti-Gravity Architecture Enforcer.
 * <p>
 * This test acts as a "Development Copilot", ensuring no developer
 * accidentally re-introduces legacy debt or architectural violations.
 */
public class ArchitectureEnforcementTest {

    private static final String ROOT_PATH = "src/main/java";

    // Forbidden patterns that indicate architectural violations
    private static final List<String> FORBIDDEN_STRINGS = List.of(
            "user-photos/",
            "products-photos/",
            "site-logo/",
            "categories-photos/",
            "brands-photos/",
            "hardcoded-path");

    @Test
    public void testNoLegacyPathsInCodebase() throws IOException {
        System.out.println("🤖 Anti-Gravity Copilot: Scanning for Architectural Violations...");

        List<Path> javaFiles;
        try (Stream<Path> walk = Files.walk(Paths.get(ROOT_PATH))) {
            javaFiles = walk.filter(p -> p.toString().endsWith(".java"))
                    .collect(Collectors.toList());
        }

        StringBuilder violations = new StringBuilder();
        int violationCount = 0;

        for (Path file : javaFiles) {
            // Skip the Auditor itself, as it needs to reference these for detection logic
            if (file.toString().contains("SystemArchitectureAuditor.java") ||
                    file.toString().contains("ArchitectureEnforcementTest.java") ||
                    file.toString().contains("KnowledgeBootstrap.java")) {
                continue;
            }

            List<String> lines = Files.readAllLines(file);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                for (String forbidden : FORBIDDEN_STRINGS) {
                    if (line.contains("\"" + forbidden + "\"") || line.contains("\"" + forbidden)) {
                        violationCount++;
                        violations.append(String.format(
                                "\n[VIOLATION] File: %s:%d\n  --> Found legacy path: '%s'\n  --> Rule: TENANT_ASSET_ISOLATION\n  --> Fix: Use FileUploadUtil.getStoragePath(id, type) instead.\n",
                                file.getFileName(), (i + 1), forbidden));
                    }
                }
            }
        }

        if (violationCount > 0) {
            Assertions.fail("\n\n" +
                    "████████████████████████████████████████████████████████\n" +
                    "█  ANTI-GRAVITY ARCHITECTURE VIOLATION DETECTED        █\n" +
                    "████████████████████████████████████████████████████████\n" +
                    violations.toString() + "\n" +
                    "The build has been BLOCKED to protect system integrity.\n" +
                    "Please apply the suggested fixes above.\n");
        } else {
            System.out.println("✅ Architecture Compliant: No legacy paths found.");
        }
    }

    @Test
    public void testControllerLayering() throws IOException {
        // Basic check to ensure Controllers don't have direct DB access (simplified)
        // Not strictly enforcing yet, but placeholder for future expansion.
    }
}

package com.onlineStore.admin.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeBootstrap {

    private final com.onlineStore.admin.knowledge.KnowledgeRegistry knowledgeRegistry;

    @org.springframework.beans.factory.annotation.Autowired
    public KnowledgeBootstrap(com.onlineStore.admin.knowledge.KnowledgeRegistry knowledgeRegistry) {
        this.knowledgeRegistry = knowledgeRegistry;
    }

    @PostConstruct
    public void loadKnowledge() {
        System.out.println("🚀 Anti-Gravity Knowledge System is starting...");

        try {
            org.springframework.core.io.support.PathMatchingResourcePatternResolver resolver = new org.springframework.core.io.support.PathMatchingResourcePatternResolver();
            org.springframework.core.io.Resource[] resources = resolver.getResources("classpath*:anti-gravity/*.yml");

            org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml(); // Generic Loader

            for (org.springframework.core.io.Resource resource : resources) {
                try {
                    loadResource(resource, yaml);
                } catch (Exception e) {
                    System.err.println(
                            "⚠️ Failed to load knowledge file: " + resource.getFilename() + " -> " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out
                    .println("✅ Knowledge System loaded successfully. Total entries: " + knowledgeRegistry.getCount());
            reorganizeAssets();
            generateHtmlPreview();
            generateFolderStructure();

        } catch (Exception e) {
            System.err.println("❌ Critical Error initializing Knowledge System: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void reorganizeAssets() {
        System.out.println("📦 Starting Asset Reorganization with Logging...");

        // Setup Logging
        java.nio.file.Path logDir = java.nio.file.Paths.get("logs", "tenant-migration");
        String logFileName = "tenant-migration-"
                + java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(java.time.LocalDateTime.now())
                + ".log";
        java.nio.file.Path logFile = logDir.resolve(logFileName);

        int tenantsProcessed = 0;
        int filesMoved = 0;
        int filesSkipped = 0;
        int errors = 0;

        try {
            java.nio.file.Files.createDirectories(logDir);
            java.nio.file.Files.writeString(logFile,
                    "=== Tenant Migration Log Started: " + java.time.LocalDateTime.now() + " ===\n",
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (java.io.IOException e) {
            System.err.println("⚠️ Follow-up logging failed: " + e.getMessage());
        }

        java.util.function.Consumer<String> logAction = (String msg) -> {
            try {
                System.out.println(msg);
                java.nio.file.Files.writeString(logFile, msg + "\n", java.nio.file.StandardOpenOption.CREATE,
                        java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception ex) {
            }
        };

        // Maps
        java.util.Map<String, String> assetTypeMap = new java.util.HashMap<>();
        assetTypeMap.put("site-logo", "profile");
        assetTypeMap.put("brands-photos", "brands");
        assetTypeMap.put("categories-photos", "categories");
        assetTypeMap.put("products-photos", "products");
        assetTypeMap.put("services-photos", "services");
        assetTypeMap.put("user-photos", "users");
        assetTypeMap.put("customers-photos", "customers");

        // Reverse Map for Intermediate Check (profile -> profile)
        java.util.Set<String> knownTypes = new java.util.HashSet<>(assetTypeMap.values());

        // Base Paths
        java.nio.file.Path basePath = java.nio.file.Paths.get("webParent", "WebBackEnd");
        if (!java.nio.file.Files.exists(basePath))
            basePath = java.nio.file.Paths.get(".");
        java.nio.file.Path tenantsRoot = basePath.resolve("tenants");

        // Set of all tenant IDs encountered
        java.util.Set<String> processedTenants = new java.util.HashSet<>();

        // 1. Process Legacy Flat Folders (site-logo/1 -> tenants/1/assets/profile)
        for (java.util.Map.Entry<String, String> entry : assetTypeMap.entrySet()) {
            String sourceName = entry.getKey();
            String targetType = entry.getValue();
            java.nio.file.Path sourceDir = basePath.resolve(sourceName);

            if (java.nio.file.Files.exists(sourceDir) && java.nio.file.Files.isDirectory(sourceDir)) {
                try (java.util.stream.Stream<java.nio.file.Path> tenantDirs = java.nio.file.Files.list(sourceDir)) {
                    // Collect to list to avoid stream issues inside loop
                    java.util.List<java.nio.file.Path> dirs = tenantDirs.filter(java.nio.file.Files::isDirectory)
                            .collect(java.util.stream.Collectors.toList());

                    for (java.nio.file.Path tenantDir : dirs) {
                        String tenantId = tenantDir.getFileName().toString();
                        processedTenants.add(tenantId);

                        java.nio.file.Path targetDir = tenantsRoot.resolve(tenantId).resolve("assets")
                                .resolve(targetType);

                        int moved = moveDirectoryContents(tenantDir, targetDir, tenantId, targetType, logAction);
                        filesMoved += moved;

                        // Cleanup
                        try {
                            if (isConnectedEmpty(tenantDir)) {
                                java.nio.file.Files.delete(tenantDir);
                            }
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                    logAction.accept("[ERROR] Failed scanning legacy dir: " + sourceDir + " -> " + e.getMessage());
                    errors++;
                }
            }
        }

        // 2. Process Intermediate Folders (tenants/1/profile ->
        // tenants/1/assets/profile)
        if (java.nio.file.Files.exists(tenantsRoot)) {
            try (java.util.stream.Stream<java.nio.file.Path> tenantRoots = java.nio.file.Files.list(tenantsRoot)) {
                java.util.List<java.nio.file.Path> tRoots = tenantRoots.filter(java.nio.file.Files::isDirectory)
                        .collect(java.util.stream.Collectors.toList());

                for (java.nio.file.Path tRoot : tRoots) {
                    String tenantId = tRoot.getFileName().toString();
                    processedTenants.add(tenantId);

                    // Scan children of tenant root
                    try (java.util.stream.Stream<java.nio.file.Path> subDirs = java.nio.file.Files.list(tRoot)) {
                        java.util.List<java.nio.file.Path> types = subDirs.filter(java.nio.file.Files::isDirectory)
                                .collect(java.util.stream.Collectors.toList());

                        for (java.nio.file.Path typeDir : types) {
                            String dirName = typeDir.getFileName().toString();

                            // If this is "assets", skip it (it's the destination)
                            if ("assets".equals(dirName))
                                continue;

                            // If this is a known type (e.g. "profile"), move it to "assets/profile"
                            if (knownTypes.contains(dirName)) {
                                java.nio.file.Path targetDir = tRoot.resolve("assets").resolve(dirName);
                                if (!typeDir.equals(targetDir)) { // Anti-recursion logic
                                    int moved = moveDirectoryContents(typeDir, targetDir, tenantId, dirName, logAction);
                                    filesMoved += moved;
                                    try {
                                        if (isConnectedEmpty(typeDir)) {
                                            java.nio.file.Files.delete(typeDir);
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logAction.accept("[ERROR] Failed scanning tenant sub-dirs: " + tRoot.getFileName() + " -> "
                                + e.getMessage());
                        errors++;
                    }
                }
            } catch (Exception e) {
                logAction.accept("[ERROR] Failed scanning tenants root: " + e.getMessage());
                errors++;
            }
        }

        tenantsProcessed = processedTenants.size();

        // Summary
        logAction.accept("=========================================");
        logAction.accept("MIGRATION SUMMARY");
        logAction.accept("Total Tenants Processed: " + tenantsProcessed);
        logAction.accept("Total Files Moved: " + filesMoved);
        logAction.accept("Errors: " + errors);
        logAction.accept("=========================================");
    }

    private boolean isConnectedEmpty(java.nio.file.Path dir) throws java.io.IOException {
        try (java.util.stream.Stream<java.nio.file.Path> list = java.nio.file.Files.list(dir)) {
            return !list.findAny().isPresent();
        }
    }

    private int moveDirectoryContents(java.nio.file.Path source, java.nio.file.Path target, String tenantId,
                                      String assetType, java.util.function.Consumer<String> logAction) {
        int count = 0;
        try {
            java.nio.file.Files.createDirectories(target);
            try (java.util.stream.Stream<java.nio.file.Path> files = java.nio.file.Files.list(source)) {
                java.util.List<java.nio.file.Path> fileList = files.collect(java.util.stream.Collectors.toList());
                for (java.nio.file.Path file : fileList) {
                    try {
                        java.nio.file.Path targetFile = target.resolve(file.getFileName());
                        String timestamp = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
                                .format(java.time.LocalDateTime.now());

                        if (java.nio.file.Files.exists(targetFile)) {
                            // Collision
                            String newName = file.getFileName().toString() + "_" + System.currentTimeMillis();
                            targetFile = target.resolve(newName);
                            logAction.accept(String.format("[%s] [%s] [%s] WARNING: Collision. Renaming %s -> %s",
                                    timestamp, tenantId, assetType, file.getFileName(), newName));
                        }

                        java.nio.file.Files.move(file, targetFile, java.nio.file.StandardCopyOption.ATOMIC_MOVE);
                        logAction.accept(String.format("[%s] [%s] [%s] %s -> %s [OK]", timestamp, tenantId, assetType,
                                source.relativize(file), target.relativize(targetFile)));
                        count++;
                    } catch (Exception e) {
                        logAction.accept(String.format("[%s] [%s] [%s] ERROR: Failed to move %s -> %s",
                                java.time.LocalTime.now(), tenantId, assetType, file.getFileName(), e.getMessage()));
                    }
                }
            }
        } catch (Exception e) {
            logAction.accept(String.format("[%s] [%s] [%s] ERROR: Target setup failed -> %s", java.time.LocalTime.now(),
                    tenantId, assetType, e.getMessage()));
        }
        return count;
    }

    private void generateHtmlPreview() {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: Segoe UI, sans-serif; padding: 20px; background: #f4f4f4; }");
        html.append("h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }");
        html.append(
                ".tenant-block { background: white; padding: 15px; margin-bottom: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }");
        html.append(
                ".tenant-header { background: #2c3e50; color: white; padding: 10px; border-radius: 4px; display: flex; justify-content: space-between; align-items: center; }");
        html.append(".type-block { margin-left: 20px; margin-top: 15px; }");
        html.append(
                ".type-header { color: #e67e22; font-weight: bold; border-bottom: 1px solid #eee; padding-bottom: 5px; margin-bottom: 10px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 5px; font-size: 14px; }");
        html.append("th { background: #ecf0f1; text-align: left; padding: 8px; color: #7f8c8d; }");
        html.append("td { border-bottom: 1px solid #ecf0f1; padding: 8px; }");
        html.append(
                ".badge { background: #3498db; color: white; padding: 2px 6px; border-radius: 4px; font-size: 11px; }");
        html.append(
                ".assets-bar { margin-top:10px; padding:10px; background:#eef; border-radius:4px; font-size: 14px; }");
        html.append(".assets-bar a { text-decoration: none; color: #3498db; font-weight: 500; margin-right: 15px; }");
        html.append(".assets-bar a:hover { text-decoration: underline; color: #2c3e50; }");
        html.append("</style></head><body>");

        html.append("<h1>🧠 Anti-Gravity Knowledge Base</h1>");

        // Grouping: Tenant -> Type -> entries
        java.util.Map<String, java.util.Map<String, java.util.List<com.onlineStore.admin.knowledge.KnowledgeEntry>>> grouped = knowledgeRegistry
                .getAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(this::extractTenant,
                        java.util.stream.Collectors
                                .groupingBy(com.onlineStore.admin.knowledge.KnowledgeEntry::getType)));

        // Discover Tenants from Assets (Check 'assets' subfolder now)
        java.util.Set<String> allTenants = new java.util.HashSet<>(grouped.keySet());

        java.nio.file.Path basePath = java.nio.file.Paths.get("webParent", "WebBackEnd");
        if (!java.nio.file.Files.exists(basePath))
            basePath = java.nio.file.Paths.get(".");
        java.nio.file.Path tenantsRoot = basePath.resolve("tenants");

        if (java.nio.file.Files.exists(tenantsRoot)) {
            try (java.util.stream.Stream<java.nio.file.Path> dirs = java.nio.file.Files.list(tenantsRoot)) {
                // Check if they have 'assets' folder or are just tenant folders
                dirs.filter(java.nio.file.Files::isDirectory)
                        .map(p -> p.getFileName().toString())
                        .forEach(allTenants::add);
            } catch (Exception e) {
            }
        }

        // Sort Tenants
        java.util.List<String> sortedTenants = new java.util.ArrayList<>(allTenants);
        sortedTenants.sort((a, b) -> {
            if (a.equals("GLOBAL"))
                return -1;
            if (b.equals("GLOBAL"))
                return 1;
            try {
                return Integer.compare(Integer.parseInt(a), Integer.parseInt(b));
            } catch (NumberFormatException e) {
                return a.compareTo(b);
            }
        });

        for (String tenant : sortedTenants) {
            html.append("<div class='tenant-block'>");
            html.append("<div class='tenant-header'><h3>").append(tenant).append("</h3></div>");

            java.util.Map<String, java.util.List<com.onlineStore.admin.knowledge.KnowledgeEntry>> types = grouped
                    .getOrDefault(tenant, java.util.Collections.emptyMap());

            // Render Knowledge Entries if any
            for (java.util.Map.Entry<String, java.util.List<com.onlineStore.admin.knowledge.KnowledgeEntry>> typeEntry : types
                    .entrySet()) {
                html.append("<div class='type-block'>");
                html.append("<div class='type-header'>").append(typeEntry.getKey()).append("</div>");
                html.append(
                        "<table><thead><tr><th>ID</th><th>Version</th><th>Personas</th><th>Action</th></tr></thead><tbody>");

                for (com.onlineStore.admin.knowledge.KnowledgeEntry entry : typeEntry.getValue()) {
                    html.append("<tr>");
                    html.append("<td><b>").append(entry.getId()).append("</b></td>");
                    html.append("<td><span class='badge'>")
                            .append(entry.getVersioning() != null ? entry.getVersioning().getVersion() : "v0.0")
                            .append("</span></td>");
                    html.append("<td>")
                            .append(entry.getPersonas() != null ? String.join(", ", entry.getPersonas()) : "All")
                            .append("</td>");
                    html.append("<td><a href='knowledge_export/tenants/").append(tenant).append("/")
                            .append(entry.getType()).append("/").append(entry.getId())
                            .append(".yml' target='_blank'>View YAML</a></td>");
                    html.append("</tr>");
                }
                html.append("</tbody></table></div>");
            }

            // Asset Links Section (Updated paths to /tenants/{id}/assets/...)
            html.append("<div class='assets-bar'>");
            html.append("<strong>📂 Assets:</strong> ");
            html.append("<a href='tenants/").append(tenant).append("/assets/profile/' target='_blank'>Profile</a>");
            html.append("<a href='tenants/").append(tenant).append("/assets/brands/' target='_blank'>Brands</a>");
            html.append("<a href='tenants/").append(tenant)
                    .append("/assets/categories/' target='_blank'>Categories</a>");
            html.append("<a href='tenants/").append(tenant).append("/assets/products/' target='_blank'>Products</a>");
            html.append("<a href='tenants/").append(tenant).append("/assets/users/' target='_blank'>Users</a>");
            html.append("</div>");

            html.append("</div>");
        }

        html.append("</body></html>");

        try (java.io.PrintWriter out = new java.io.PrintWriter("knowledge_preview.html")) {
            out.println(html.toString());
            System.out.println("📄 Generated HTML Preview: knowledge_preview.html");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to generate HTML preview: " + e.getMessage());
        }
    }

    private void generateFolderStructure() {
        try {
            java.nio.file.Path root = java.nio.file.Paths.get("knowledge_export", "tenants");
            // Clear old export if exists? Maybe dangerous. Just overwrite/merge.
            java.nio.file.Files.createDirectories(root);

            org.yaml.snakeyaml.Yaml yamlDumper = new org.yaml.snakeyaml.Yaml();

            for (com.onlineStore.admin.knowledge.KnowledgeEntry entry : knowledgeRegistry.getAll()) {
                String tenant = extractTenant(entry);
                String type = entry.getType() != null ? entry.getType() : "UNKNOWN";
                String filename = entry.getId() + ".yml";

                java.nio.file.Path typeDir = root.resolve(tenant).resolve(type);
                java.nio.file.Files.createDirectories(typeDir);

                java.nio.file.Path filePath = typeDir.resolve(filename);

                // Reconstruct a map to dump reasonably
                java.util.Map<String, Object> dumpMap = new java.util.LinkedHashMap<>();
                dumpMap.put("id", entry.getId());
                dumpMap.put("type", entry.getType());
                if (entry.getVersioning() != null) {
                    dumpMap.put("versioning", entry.getVersioning());
                }
                dumpMap.put("personas", entry.getPersonas());
                if (entry.getContent() != null) {
                    dumpMap.put("content", entry.getContent());
                }

                // SnakeYAML dump
                try (java.io.Writer writer = java.nio.file.Files.newBufferedWriter(filePath)) {
                    yamlDumper.dump(dumpMap, writer);
                }
            }
            System.out.println("📂 Generated Folder Structure: " + root.toAbsolutePath());

        } catch (Exception e) {
            System.err.println("⚠️ Failed to generate folder structure: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String extractTenant(com.onlineStore.admin.knowledge.KnowledgeEntry entry) {
        if (entry.getContent() != null && entry.getContent().containsKey("tenantId")) {
            return String.valueOf(entry.getContent().get("tenantId"));
        }
        // Fallback checks
        if (entry.getId().contains("TENANT-")) {
            // naive check
        }
        return "GLOBAL";
    }

    private void loadResource(org.springframework.core.io.Resource resource, org.yaml.snakeyaml.Yaml yaml)
            throws java.io.IOException {
        if (!resource.exists()) {
            return;
        }

        System.out.println("📘 Loading knowledge file: " + resource.getFilename());
        try (java.io.InputStream inputStream = resource.getInputStream()) {
            Object loaded = yaml.load(inputStream);

            if (loaded instanceof java.util.Map) {
                java.util.Map<String, Object> data = (java.util.Map<String, Object>) loaded;
                com.onlineStore.admin.knowledge.KnowledgeEntry entry = adaptMapToEntry(data, resource.getFilename());

                if (entry != null && entry.getId() != null) {
                    knowledgeRegistry.register(entry);
                    System.out.println("   + Registered: " + entry.getId() + " (" + entry.getType() + ")");
                } else {
                    System.err.println(
                            "   ⚠️ Skipping " + resource.getFilename() + ": Could not adapt to KnowledgeEntry.");
                }
            } else {
                System.err.println("   ⚠️ Skipping " + resource.getFilename() + ": Content is not a YAML Map.");
            }
        }
    }

    private com.onlineStore.admin.knowledge.KnowledgeEntry adaptMapToEntry(java.util.Map<String, Object> data,
                                                                           String filename) {
        com.onlineStore.admin.knowledge.KnowledgeEntry entry = new com.onlineStore.admin.knowledge.KnowledgeEntry();

        // Check if it's already in the correct structure
        if (data.containsKey("id") && data.containsKey("type") && data.containsKey("content")) {
            entry.setId((String) data.getOrDefault("id", generateId(filename)));
            entry.setType((String) data.getOrDefault("type", "UNKNOWN"));
            entry.setPersonas((java.util.List<String>) data.get("personas"));
            entry.setContent((java.util.Map<String, Object>) data.get("content"));

            if (data.containsKey("versioning")) {
                java.util.Map<String, Object> verMap = (java.util.Map<String, Object>) data.get("versioning");
                com.onlineStore.admin.knowledge.KnowledgeVersion ver = new com.onlineStore.admin.knowledge.KnowledgeVersion();
                ver.setVersion((String) verMap.get("version"));
                ver.setPreviousVersion((String) verMap.get("previousVersion"));
                ver.setUpdatedAt((String) verMap.get("updatedAt"));
                ver.setBackwardCompatible((Boolean) verMap.getOrDefault("backwardCompatible", true));
                entry.setVersioning(ver);
            }
        } else {
            // Unstructured file - Wrap content
            entry.setId(generateId(filename));
            // AG-KNOWLEDGE-TYPE-001: Intelligent type classification based on content
            String detectedType = detectTypeFromContent(data, filename);
            entry.setType(detectedType);
            entry.setContent(data);

            // Set default version
            com.onlineStore.admin.knowledge.KnowledgeVersion ver = new com.onlineStore.admin.knowledge.KnowledgeVersion();
            ver.setVersion("0.1.0");
            ver.setUpdatedAt(java.time.LocalDateTime.now().toString());
            entry.setVersioning(ver);
        }
        return entry;
    }

    private String generateId(String filename) {
        if (filename == null)
            return "UNKNOWN-" + System.currentTimeMillis();
        // Remove extension and sanitize
        return filename.toUpperCase().replace(".YML", "").replace(".YAML", "").replace(" ", "-");
    }

    /**
     * AG-KNOWLEDGE-TYPE-002: Intelligent type detection from YAML content
     * Detects knowledge type based on structure and keywords in content
     */
    private String detectTypeFromContent(java.util.Map<String, Object> data, String filename) {
        // Check for explicit type markers in content
        if (data.containsKey("rule")) {
            return "RULE";
        }
        if (data.containsKey("architecture") || data.containsKey("design")) {
            return "ARCHITECTURE";
        }
        if (data.containsKey("decision") || filename.contains("decision")) {
            return "DESIGN_DECISION";
        }
        // AG-KNOWLEDGE-TYPE-003: Check for business/scenario keywords in any key
        if (hasKeyContaining(data, "business") || hasKeyContaining(data, "scenario")) {
            return "BUSINESS_LOGIC";
        }
        if (data.containsKey("governance") || data.containsKey("policy")) {
            return "GOVERNANCE";
        }
        if (filename.contains("principle")) {
            return "PRINCIPLE";
        }
        if (filename.contains("intent") || filename.contains("purpose")) {
            return "INTENT";
        }
        if (filename.contains("storage") || filename.contains("structure")) {
            return "TECHNICAL_SPEC";
        }
        if (filename.contains("prompt") || filename.contains("ai")) {
            return "AI_CONTEXT";
        }
        if (filename.contains("pitch") || filename.contains("deck")) {
            return "BUSINESS_PITCH";
        }
        if (filename.contains("reference") || filename.contains("data")) {
            return "REFERENCE_DATA";
        }
        if (filename.contains("mapping")) {
            return "MAPPING";
        }


        // Default fallback
        return "UNSTRUCTURED";
    }

    /**
     * AG-KNOWLEDGE-TYPE-004: Helper to check if any key contains a keyword
     * Makes detection more flexible for variations like "business_scenarios"
     */
    private boolean hasKeyContaining(java.util.Map<String, Object> data, String keyword) {
        return data.keySet().stream()
                .anyMatch(key -> key.toLowerCase().contains(keyword.toLowerCase()));
    }
}

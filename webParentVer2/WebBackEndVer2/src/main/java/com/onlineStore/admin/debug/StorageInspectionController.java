package com.onlineStore.admin.debug;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/admin/debug/storage")
public class StorageInspectionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageInspectionController.class);
    private static final String DUMP_PATH = "E:/onlineStore_Active/dumps/";

    @GetMapping("/dump")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> dumpStorage(
            @RequestParam("tenantId") Long tenantId,
            @RequestParam(value = "structured", defaultValue = "true") boolean structured) {

        Map<String, Object> response = new HashMap<>();
        response.put("tenantId", tenantId);
        response.put("timestamp", new Date());

        String rootPathStr = "tenants/" + tenantId;
        Path rootPath = Paths.get(rootPathStr);

        if (!Files.exists(rootPath)) {
            response.put("error", "Tenant storage root not found: " + rootPathStr);
            return response;
        }

        try {
            Object dumpData;
            if (structured) {
                dumpData = buildDirectoryTree(rootPath.toFile());
            } else {
                try (Stream<Path> walk = Files.walk(rootPath)) {
                    dumpData = walk.filter(Files::isRegularFile)
                            .map(Path::toString)
                            .collect(Collectors.toList());
                }
            }

            response.put("data", dumpData);

            // Save to disk
            saveDumpToDisk(response, tenantId);
            response.put("savedTo", DUMP_PATH);

        } catch (IOException e) {
            response.put("error", "Failed to walk directory: " + e.getMessage());
            LOGGER.error("Dump failed", e);
        }

        return response;
    }

    private Map<String, Object> buildDirectoryTree(File folder) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("name", folder.getName());
        node.put("path", folder.getPath());

        if (folder.isDirectory()) {
            node.put("type", "directory");
            List<Object> children = new ArrayList<>();
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    children.add(buildDirectoryTree(file));
                }
            }
            node.put("children", children);
        } else {
            node.put("type", "file");
            node.put("size", folder.length());
        }
        return node;
    }

    private void saveDumpToDisk(Map<String, Object> data, Long tenantId) {
        try {
            File dumpDir = new File(DUMP_PATH);
            if (!dumpDir.exists()) {
                dumpDir.mkdirs();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String filename = "storage_dump_tenant_" + tenantId + "_" + sdf.format(new Date()) + ".json";
            File file = new File(dumpDir, filename);

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(file, data);

            LOGGER.info("Dump saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Failed to save dump to disk", e);
        }
    }
}

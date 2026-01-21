package com.onlineStore.admin.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;

@Controller
@RequestMapping("/admin/system")
public class SystemHealthController {

    private final HealthEndpoint healthEndpoint;

    @Autowired
    public SystemHealthController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/health")
    public String viewHealth(Model model) {
        // System Health
        HealthComponent health = healthEndpoint.health();
        model.addAttribute("healthStatus", health.getStatus());
        model.addAttribute("healthDetails", health);

        // Memory
        File root = new File("/");
        long totalSpace = root.getTotalSpace();
        long freeSpace = root.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;

        model.addAttribute("diskTotal", formatBytes(totalSpace));
        model.addAttribute("diskUsed", formatBytes(usedSpace));
        model.addAttribute("diskFree", formatBytes(freeSpace));
        model.addAttribute("diskUsagePercent", (int) ((double) usedSpace / totalSpace * 100));

        // JVM Memory
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = allocatedMemory - freeMemory;

        model.addAttribute("jvmMax", formatBytes(maxMemory));
        model.addAttribute("jvmUsed", formatBytes(usedMemory));
        model.addAttribute("jvmUsagePercent", (int) ((double) usedMemory / maxMemory * 100));

        // Threads
        int activeThreads = Thread.activeCount();
        model.addAttribute("activeThreads", activeThreads);

        return "system/health";
    }

    private String formatBytes(long bytes) {
        long limit = 10 * 1024;
        long limit2 = limit * 2 * 1024;
        long limit3 = limit2 * 2 * 1024;
        if (bytes < limit)
            return bytes + " B";
        else if (bytes < limit2)
            return String.format("%.2f KB", (double) bytes / 1024);
        else if (bytes < limit3)
            return String.format("%.2f MB", (double) bytes / (1024 * 1024));
        else
            return String.format("%.2f GB", (double) bytes / (1024 * 1024 * 1024));
    }
}

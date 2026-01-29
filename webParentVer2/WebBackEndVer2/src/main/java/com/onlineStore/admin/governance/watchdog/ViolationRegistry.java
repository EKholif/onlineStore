package com.onlineStore.admin.governance.watchdog;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registry to store architectural violations detected at runtime.
 * Thread-safe implementation using CopyOnWriteArrayList.
 */
@Component
public class ViolationRegistry {

    // Thread-safe list for concurrent access by WatchService thread and HTTP
    // requests (reports)
    private final List<Violation> violations = new CopyOnWriteArrayList<>();

    public void addViolation(String type, String path, String description) {
        Violation v = new Violation(LocalDateTime.now(), type, path, description);
        violations.add(v);
        // Keep list size manageable in memory? For now, unlimited as this is MVP.
    }

    public List<Violation> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    public void clear() {
        violations.clear();
    }

    public static class Violation {
        private final LocalDateTime timestamp;
        private final String type;
        private final String path;
        private final String description;

        public Violation(LocalDateTime timestamp, String type, String path, String description) {
            this.timestamp = timestamp;
            this.type = type;
            this.path = path;
            this.description = description;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String getType() {
            return type;
        }

        public String getPath() {
            return path;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s: %s (%s)", timestamp, type, description, path);
        }
    }
}

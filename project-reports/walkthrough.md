# Architecture Watchdog Implementation Walkthrough

## Overview

I have implemented a runtime **Architecture Watchdog** that proactively monitors the application for architectural
violations, specifically focusing on Multi-Tenancy isolation and folder structure integrity.

## Changes Implemented

### 1. Core Watchdog Service

- **`ArchitectureWatchdogService`**: A new Spring Service that runs primarily in the background.
- **`FileSystemMonitor`**: A daemon task using Java's `WatchService` to monitor the file system in real-time.
    - **Monitors**:
        - Root directory `.` for prohibited legacy folders (e.g., `user-photos`, `site-logo`).
        - `tenants/` directory for structure violations.
    - **Detects**: `ENTRY_CREATE` events matching forbidden patterns.

### 2. Violation Reporting

- **`ViolationRegistry`**: A central component to record validation failures.
- **Reports**:
    - **`runtime_violation_report.md`**: Append-only log of violations (Markdown).
    - **`watchdog_status_report.html`**: A live HTML dashboard showing current violation status.

### 3. Asset Operation Interception

- **`FileUploadUtil` Hardening**:
    - Injected `validatePath(String path)` into the core file utility.
    - **Tenant Isolation**: Verifies that any write to `tenants/{id}/...` matches the current `TenantContext`.
    - **Structure Enforcement**: Blocks writes to paths not starting with `tenants/` (except specific exceptions).
    - **Secured Methods**: `saveFile`, `cleanDir`, `deleteDir` are now guarded.

## Verification

### Automated Tests

I created and ran the following integration tests:

- **`ArchitectureWatchdogTest`**:
    - Starts the watchdog on a temporary directory.
    - Programmatically creates a prohibited folder (`site-logo`).
    - **Result**: Watchdog detected the violation and logged it to the registry.
- **`FileUploadUtilTest`**:
    - Attempts to call `cleanDir` on a legacy path (`site-logo/1`).
    - **Result**: Threw `IllegalArgumentException` with "Architecture Violation".

### Proof of Enforcement

```
[INFO] Running com.onlineStore.admin.governance.watchdog.ArchitectureWatchdogTest
...
🚨 ARCHITECTURE VIOLATION [LEGACY_PATH_CREATION] Prohibited legacy folder created: ...\site-logo
...
[INFO] Running com.onlineStore.admin.utility.FileUploadUtilTest
...
🚨 ARCHITECTURE VIOLATION: Attempt to write to prohibited path: site-logo/1
```

## Next Steps

- **Refactor Advisor**: The Watchdog now provides the data needed for the AI Refactor Advisor to identify and suggest
  cleanups.
- **Production**: Ensure the `ArchitectureWatchdogService` thread is monitored and doesn't consume excessive CPU (it
  uses blocking `poll`, so it is efficient).

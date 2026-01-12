# Architecture Watchdog Implementation Plan

## Goal Description

Implement a runtime "Architecture Watchdog" service that continuously monitors the application's file system and
internal state to detect and report architectural violations. This ensures that the system adheres to strict
multi-tenancy and structure rules during operation, not just at build or startup time.

## User Review Required

> [!IMPORTANT]
> This feature introduces a runtime file system watcher (`java.nio.file.WatchService`). While standard in Java,
> monitoring a large number of directories can consume resources. We will limit monitoring to specific root
> directories (`tenants/`, `webParent/WebBackEnd/` legacy roots).

> [!NOTE]
> The watchdog will generate reports and logs. We need to ensure these don't flood the disk if violations are frequent.
> Rate limiting on logging will be considered.

## Proposed Changes

### 1. New Service: `ArchitectureWatchdog`

Create `com.onlineStore.admin.governance.watchdog.ArchitectureWatchdogService` in `WebBackEnd`.
This service will:

- Start a separate daemon thread to run the `WatchService`.
- Register watchers for `tenants/` directory (recursive where possible/needed, or specifically watching for new tenant
  creations).
- Register watchers for the root directory to detect banned folders (e.g., `user-photos`, `site-logo` created at root).

### 2. File System Monitoring (`FileSystemMonitor`)

- Use `java.nio.file.WatchService` to listen for `ENTRY_CREATE`, `ENTRY_MODIFY` events.
- **Rule Enforcement**:
    - If a folder is created in root that matches legacy patterns (`*-photos`), flag a violation.
    - If a file is added to `assets` outside of `tenants/{id}/assets/...`, flag a violation.

### 3. Asset Operation Interception

- Create an AOP Aspect or Event Listener `AssetOperationAuditor` to scrutinize calls to file storage utilities (
  e.g., `FileUploadUtil`, `AmazonS3Util` if exists).
- Checks:
    - Verify target path contains `tenants/{id}/assets/`.
    - Verify `TenantContext` matches the path's tenant ID.

### 4. Reporting & Logging

- **Immediate**: Log to SLF4J with a specific marker `ARCHITECTURE_VIOLATION`.
- **Periodic**: Update `runtime_violation_report.md` and `watchdog_status_report.html` (appending new violations).
- **History**: Keep an in-memory or file-based list of recent violations for the HTML report.

### Directory Structure

```
com.onlineStore.admin.governance.watchdog
├── ArchitectureWatchdogService.java (Main @Service)
├── FileSystemWatcher.java (Runnable for WatchService)
├── ViolationRegistry.java (Stores history)
└── AssetOperationAspect.java (AOP for file ops if feasible, else simpler service wrapper)
```

## Verification Plan

### Automated Verification

- **Integration Test**: Create `ArchitectureWatchdogTest`.
    - Start the `ArchitectureWatchdogService`.
    - Programmatically create a "legacy" folder (e.g., `target/test-roots/site-logo`) in a watched test directory.
    - Assert that the violation is logged and added to the registry within a few seconds.
    - Create a valid tenant file and assert NO violation is logged.

### Manual Verification

- Start the application.
- Manually create a prohibited folder (e.g., `webParent/WebBackEnd/test-legacy-folder`) using the OS file explorer.
- Check the console logs for the violation alert.
- Check `watchdog_status_report.html` for the new entry.

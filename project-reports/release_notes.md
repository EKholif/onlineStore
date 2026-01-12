# Release Notes: Architecture Watchdog & Security Hardening

**Release Date**: 2026-01-12
**Version**: 1.1.0-WATCHDOG
**Status**: deployed-to-dev

## 🚀 Summary

This release introduces the **Architecture Watchdog**, a runtime monitoring subsystem designed to detect and prevent
architectural drift. It also includes critical security hardening of file operation utilities to enforce strict
multi-tenant isolation.

## 🛡️ Security & Compliance

- **Feature**: Runtime Architecture Enforcement
    - **Why**: To prevent the re-introduction of legacy path patterns (e.g., `user-photos`, `site-logo`) that violate
      tenant isolation rules.
    - **Mechanism**: `ArchitectureWatchdogService` runs a background `FileSystemMonitor` using Java NIO `WatchService`
      on the system root and `tenants/` directory.
    - **Impact**: Any directory created matching a prohibited pattern triggers a logged violation and alert.
    - **Rule**: `TENANT_ASSET_ISOLATION`

- **Feature**: Strict Tenant Isolation in File Operations
    - **Why**: Previous `FileUploadUtil` allowed arbitrary paths, posing a risk of cross-tenant data leakage.
    - **Change**: Injected `validatePath(String)` into `com.onlineStore.admin.utility.FileUploadUtil`.
    - **Enforcement**:
        - **Rule 1**: All paths MUST start with `tenants/` (or `knowledge_export`).
        - **Rule 2**: Paths MUST match the current `TenantContext` ID.
    - **Impact**: Code attempting to write outside the tenant's sandbox will now throw `SecurityException`
      or `IllegalArgumentException`.

## 🛠️ Technical Changes

### New Components

- `com.onlineStore.admin.governance.watchdog.ArchitectureWatchdogService`: Lifecycle manager for monitoring.
- `com.onlineStore.admin.governance.watchdog.FileSystemMonitor`: High-performance NIO file watcher.
- `com.onlineStore.admin.governance.watchdog.ViolationRegistry`: In-memory and disk-based registry for architectural
  violations.

### Modified Components

- `com.onlineStore.admin.utility.FileUploadUtil`:
    - Added `validatePath` check to `saveFile`, `cleanDir`, `deleteDir`.

### Artifacts Implemented

- **Runtime Reports**:
    - `runtime_violation_report.md`: Persistent log of violations.
    - `watchdog_status_report.html`: Live view of system health.

## ⚠️ Migration & Developer Notes

- **Breaking Change**: Any legacy code or tests attempting to write to root folders (e.g., `user-photos/`) will now *
  *FAIL** with an exception.
- **Action Required**: Ensure `TenantContext` is correctly set before performing any file operations.
- **False Positives**: If legitimate system processes need to write to non-tenant paths, an exception request must be
  filed to update the `ArchitectureWatchdog` whitelist.

## 🧪 Verification

- **Tests**: `ArchitectureWatchdogTest` and `FileUploadUtilTest` confirmed detection of violations and enforcement of
  rules.
- **Audit**: Refactor Advisor scanned the implementation and confirmed alignment with core architectural principles.

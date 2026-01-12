# AI Refactor Advisor Recommendations

**Status**: Generated following Architecture Watchdog Implementation
**Date**: 2026-01-12

## 1. Eliminate Redundant Utilities

- **Issue**: Duplicate `FileUploadUtil` classes found.
    - `com.onlineStore.admin.utility.FileUploadUtil` (Primary)
    - `com.onlineStore.admin.category.controller.utility.FileUploadUtil` (Likely Redundant)
- **Risk**: Medium. If the redundant utility is used, it might bypass newer security checks (though I patched it purely
  defensively, it should be removed).
- **Recommendation**:
    1. Verify usages of the `category` package utility.
    2. Refactor all calls to use the primary `admin.utility.FileUploadUtil`.
    3. Delete `com.onlineStore.admin.category.controller.utility.FileUploadUtil`.

## 2. Dynamic Rule Loading

- **Issue**: `FileSystemMonitor` has hardcoded "Prohibited Folders" (`user-photos`, `site-logo`, etc.).
- **Risk**: Low (Maintenance). If new legacy patterns emerge, code change is required.
- **Recommendation**:
    - Integrate `KnowledgeRegistry` with `ArchitectureWatchdogService`.
    - Define prohibited patterns in `knowledge/governance/architecture-rules.yml`.
    - Watchdog should reload rules on change.

## 3. Centralize Path Logic

- **Issue**: Path construction (`tenants/` + id + `/assets`) is scattered in `FileUploadUtil` and implied
  in `FileSystemMonitor`.
- **Risk**: Medium. Inconsistency could lead to `FileSystemMonitor` flagging valid paths or missing invalid ones.
- **Recommendation**:
    - Create a `PathStrategy` or `AssetLocationService`.
    - All components should ask this service: "Is this path valid?" or "Get path for User X".

## 4. Asynchronous Audit Logging

- **Issue**: `FileSystemMonitor` and `FileUploadUtil` log synchronously to `ViolationRegistry`, which writes to disk
  synchronously.
- **Risk**: Performance (IO blocking on critical paths).
- **Recommendation**:
    - Make `ViolationRegistry` use an async queue for disk writes.

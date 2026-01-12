# Anti-Gravity Business Scenarios

## 1. Scenario: The "Rogue Consultant" Protection

**Context**: A new developer or consultant joins the team and accidentally tries to upload site logos to a legacy
folder `/site-logo` in the project root, bypassing the multi-tenant structure.

**Knowledge Reference**:

- **Rule**: `TENANT_ASSET_ISOLATION` (AG-RULE-ISO-001)
- **Principle**: `SECURE_BY_DEFAULT`

**Action**:

- Consultant code executes: `FileUploadUtil.saveFile("site-logo/1", ...)`
- **Watchdog Response**:
    1. Intercepts the call via `FileUploadUtil.validatePath()`.
    2. Identifies violation: Path "site-logo/1" does not start with "tenants/".
    3. Blocks operation with `IllegalArgumentException`.
    4. Logs violation to `runtime_violation_report.md`.

**Outcome**:

- **System**: Remains clean. No rogue folders created.
- **Developer**: Receives immediate feedback ("Architecture Violation").
- **Business**: Zero risk of asset leakage between tenants.

---

## 2. Scenario: Cross-Tenant Data Leak Prevention

**Context**: A bug in the "Bulk Product Import" feature attempts to save Product images for Tenant A using Tenant B's ID
in the path (e.g., `tenants/2/assets` while logged in as Tenant 1).

**Action**:

- System executes save with path: `tenants/2/assets/products/...`
- **Security Check**:
    1. `FileUploadUtil` checks `TenantContext.getTenantId()` (Result: 1).
    2. Compares with target path ID (Result: 2).
    3. Detects mismatch.

**Outcome**:

- **Operation**: BLOCKED with `SecurityException`.
- **Audit**: Security alert logged.
- **Tenant B**: Protected. Their asset folder is untouched.

---

## 3. Scenario: Live Architecture Monitoring

**Context**: An automated deployment script or a manual admin action creates a `user-photos` folder in the production
root directory.

**Action**:

- Folder `user-photos` created on disk.
- **Architecture Watchdog** (running in background):
    1. Receives file system event `ENTRY_CREATE`.
    2. Matches name against `PROHIBITED_ROOT_DIRS`.
    3. **Alert**: Logs "LEGACY_PATH_CREATION" violation.
    4. Updates **Live Dashboard** (`watchdog_status_report.html`).

**Outcome**:

- **Ops Team**: Sees the red alert on the status dashboard.
- **Resolution**: Immediate cleanup triggered before data accumulates.

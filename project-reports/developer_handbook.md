# Developer Handbook: Anti-Gravity Architecture

**Strict Governance & Multi-Tenancy Guide**

## 1. Core Philosophy: "Business Before Code"

Every line of code in this platform must answer to a specific business rule defined in the Knowledge
System (`src/main/resources/anti-gravity`).

- **Forbidden**: Code-first changes without rule updates.
- **Mandatory**: Check `anti-gravity/*.yml` before implementing new features.

## 2. Multi-Tenancy & Isolation

This is a **Shared Database, Isolated Schema** (logical) architecture.

- **Tenant Context**: The `TenantContext` holds the current request's Tenant ID.
- **Data Access**: All Repositories MUST respect the current `TenantContext`.
- **Assets**:
    - **WRONG**: `user-photos/123/profile.jpg` (Root-level folder)
    - **CORRECT**: `tenants/{tenantId}/assets/users/{userId}/...`

> "Multi-Tenancy is the system's identity." (01-core-principles.yml)

## 3. Storage Rules (Anti-Drift)

We enforce strict folder hierarchy. See `10-storage-structure.yml`.

### The "Watchdog" 🐶

A background service (`ArchitectureWatchdogService`) monitors the file system in real-time.

- **Triggers**: Creating folders like `site-logo`, `user-photos` in the root.
- **Consequence**: Immediate violation log + Dashboard Alert.
- **Fix**: Use `FileUploadUtil.validatePath()` and ensuring Entity paths start with `tenants/`.

### File Operations

When saving files, you **MUST** use the Entity's centralized path logic:

```java
// BAD - Prohibited
FileUploadUtil.saveFile("site-logo/" + id, filename, file);

// GOOD - Compliant
String uploadDir = user.getImageDir(); // Returns "tenants/1/assets/users/..."
FileUploadUtil.saveFile(uploadDir, filename, file);
```

## 4. Governance & Compliance

- **Feature Addition**:
    1. Update/Add Rule in Knowledge YAML.
    2. Update Rule Mapping.
    3. Implement Code.
- **Compliance Check**: The system is audited. If code exists without a mapped rule, it is considered "Shadow Logic".

## 5. Troubleshooting Violations

**Error**: `java.lang.IllegalArgumentException: Architecture Violation`
**Cause**: You tried to write to a path not starting with `tenants/` or `knowledge_export/`.
**Solution**: Check your Entity's `get...Path()` method. Ensure it includes the Tenant ID.

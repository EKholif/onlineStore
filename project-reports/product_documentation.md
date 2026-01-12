# Anti-Gravity Product Documentation

**Version**: 1.0.0 (Knowledge-Driven)

## 1. System Overview

**Anti-Gravity** is an AI-Ready, Multi-Tenant SaaS Platform built on Spring Boot.
**Intent**: To create a fully extendable, "white-box" platform where every architectural decision is documented as
data (Knowledge) rather than just code.

## 2. Architecture

### 2.1 Governance Layer

The system is unique because it contains its own "Constitution" in `src/main/resources/anti-gravity`.

- **Knowledge Registry**: Loads rules at startup.
- **Architecture Watchdog**: Enforces rules at runtime.

### 2.2 Storage Architecture

*Ref: 10-storage-structure.yml*
The platform uses a **Strict Hierarchy / Tenant Isolation** model.

- **Root Storage**: `/tenants`
- **Tenant Scope**: `/tenants/{tenantId}`
- **Assets**: `/tenants/{tenantId}/assets/{domain}` (e.g., users, products)

### 2.3 Security Model

- **Secure by Default-**: APIs and File Operations reject unsafe requests automatically.
- **Tenant Context**: Thread-local context ensures data isolation per request.
- **Cross-Tenant Protection**: File writes are validated against the active Tenant Context.

## 3. Operational Behavior

### Automated Auditing

The system performs self-checks:

1. **Boot Time**: Verifies Rule Mappings.
2. **Runtime**: Monitors Disk I/O for drift.
3. **On-Demand**: Generates `system_audit` reports (HTML).

### Common Alerts

- **LEGACY_PATH_CREATION**: Detected a folder from the old single-tenant architecture.
- **CROSS_TENANT_WRITE**: Attempted to write data to a different tenant's bucket.

## 4. For Stakeholders

This approach guarantees:

- **Investability**: The Codebase is clean, documented, and safe.
- **Scalability**: New tenants are strictly isolated, allowing easy horizontal scaling.
- **AI Readiness**: The structured "Knowledge" allows AI agents to understand and refactor the system safely.

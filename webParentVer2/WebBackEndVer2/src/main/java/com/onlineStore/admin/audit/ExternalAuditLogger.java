package com.onlineStore.admin.audit;

import com.onlineStoreCom.entity.audit.TenantAuditLog;

/**
 * AG-AUDIT-EXT-001: Optional External Audit Logger Interface
 * <p>
 * Purpose:
 * - Integration point for external logging systems (ELK, Graylog, Splunk)
 * - Sends audit logs to centralized security monitoring
 * - Decouples core audit logic from external dependencies
 * <p>
 * Implementation:
 * - Create a concrete implementation class (e.g., ElkAuditLogger)
 * - Register as Spring Bean
 * - AuditService will auto-detect and use it
 * <p>
 * Example:
 * <pre>
 * @Service
 * public class ElkAuditLogger implements ExternalAuditLogger {
 *     @Override
 *     public void logTenantSwitch(TenantAuditLog log) {
 *         // Send to ELK via HTTP or Logstash
 *     }
 * }
 * </pre>
 */
public interface ExternalAuditLogger {

    /**
     * Send tenant switch event to external system
     */
    void logTenantSwitch(TenantAuditLog log);

    /**
     * Send access denied event to external system
     */
    void logAccessDenied(TenantAuditLog log);

    /**
     * Send impersonation event to external system
     */
    void logImpersonation(TenantAuditLog log);
}

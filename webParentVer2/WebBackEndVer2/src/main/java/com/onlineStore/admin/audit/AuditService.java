package com.onlineStore.admin.audit;

import com.onlineStoreCom.entity.audit.TenantAuditLog;
import com.onlineStoreCom.entity.tenant.Tenant;
import com.onlineStoreCom.entity.users.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AG-AUDIT-003: Audit Service for Security Event Logging
 * <p>
 * Purpose:
 * - Centralized audit logging for all security events
 * - Captures IP address, User-Agent, and context
 * - Optional integration with external logging systems (ELK, Graylog)
 * <p>
 * Business Impact:
 * - Security compliance (GDPR Article 30, SOC2)
 * - Incident response and forensics
 * - User activity monitoring
 */
@Service
@Transactional
public class AuditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditService.class);

    @Autowired
    private TenantAuditLogRepository auditLogRepo;

    @Autowired(required = false)
    private ExternalAuditLogger externalLogger; // Optional ELK/Graylog integration

    /**
     * AG-AUDIT-SWITCH-001: Log tenant context switch
     * <p>
     * Called when:
     * - Platform Admin switches to another tenant
     * - Agency Admin switches to child tenant
     * - Admin returns to original tenant
     */
    public void logTenantSwitch(User user, Tenant fromTenant, Tenant toTenant,
                                HttpServletRequest request) {
        TenantAuditLog log = new TenantAuditLog(user, fromTenant, "SWITCH_TENANT");
        log.setTargetTenant(toTenant);
        log.setIpAddress(getClientIp(request));
        log.setUserAgent(getUserAgent(request));
        log.setDetails(buildSwitchDetails(fromTenant, toTenant));

        auditLogRepo.save(log);

        LOGGER.info("User {} switched from Tenant {} to Tenant {}",
                user.getEmail(), fromTenant.getId(), toTenant.getId());

        if (externalLogger != null) {
            externalLogger.logTenantSwitch(log);
        }
    }

    /**
     * AG-AUDIT-DENIED-001: Log access denied events
     * <p>
     * Called when:
     * - User attempts to access unauthorized tenant
     * - User tries to switch to forbidden tenant
     * - Permission check fails
     */
    public void logAccessDenied(User user, Tenant currentTenant, Tenant targetTenant,
                                String reason, HttpServletRequest request) {
        TenantAuditLog log = new TenantAuditLog(user, currentTenant, "ACCESS_DENIED");
        log.setTargetTenant(targetTenant);
        log.setIpAddress(getClientIp(request));
        log.setUserAgent(getUserAgent(request));
        log.setDetails(buildAccessDeniedDetails(reason, request));

        auditLogRepo.save(log);

        LOGGER.warn("Access denied for User {} to Tenant {}: {}",
                user.getEmail(),
                targetTenant != null ? targetTenant.getId() : "null",
                reason);

        if (externalLogger != null) {
            externalLogger.logAccessDenied(log);
        }
    }

    /**
     * AG-AUDIT-IMPERSONATE-001: Log impersonation events
     * <p>
     * Called when:
     * - Platform Admin impersonates another user
     * - Support staff accesses tenant on behalf of user
     */
    public void logImpersonation(User admin, User targetUser, Tenant tenant,
                                 HttpServletRequest request) {
        TenantAuditLog log = new TenantAuditLog(admin, tenant, "IMPERSONATE");
        log.setIpAddress(getClientIp(request));
        log.setUserAgent(getUserAgent(request));
        log.setDetails(buildImpersonationDetails(targetUser));

        auditLogRepo.save(log);

        LOGGER.warn("Admin {} impersonated User {} on Tenant {}",
                admin.getEmail(), targetUser.getEmail(), tenant.getId());

        if (externalLogger != null) {
            externalLogger.logImpersonation(log);
        }
    }

    /**
     * Extract client IP address (handles proxy headers)
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Extract User-Agent from request
     */
    private String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && userAgent.length() > 500) {
            userAgent = userAgent.substring(0, 500); // Truncate to column limit
        }
        return userAgent;
    }

    /**
     * Build JSON details for tenant switch
     */
    private String buildSwitchDetails(Tenant from, Tenant to) {
        return String.format("{\"from_tenant_id\":%d,\"from_tenant_name\":\"%s\",\"to_tenant_id\":%d,\"to_tenant_name\":\"%s\"}",
                from.getId(), escapeJson(from.getName()),
                to.getId(), escapeJson(to.getName()));
    }

    /**
     * Build JSON details for access denied
     */
    private String buildAccessDeniedDetails(String reason, HttpServletRequest request) {
        return String.format("{\"reason\":\"%s\",\"http_method\":\"%s\",\"request_uri\":\"%s\"}",
                escapeJson(reason),
                request.getMethod(),
                escapeJson(request.getRequestURI()));
    }

    /**
     * Build JSON details for impersonation
     */
    private String buildImpersonationDetails(User targetUser) {
        return String.format("{\"target_user_id\":%d,\"target_user_email\":\"%s\"}",
                targetUser.getId(), escapeJson(targetUser.getEmail()));
    }

    /**
     * Escape JSON string values
     */
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}

package com.onlineStoreCom.infra.observability;

import org.springframework.context.annotation.Configuration;

/**
 * AG-OBS-CONFIG-001: Tracing Configuration
 * <p>
 * Purpose:
 * - Configures distributed tracing with tenant-aware baggage propagation
 * - Ensures tenant-id travels with trace across all service boundaries
 * - Enables correlation of logs and traces with tenant context
 * <p>
 * WHY: In a multi-tenant system, every trace must be attributable to a specific tenant
 * for security auditing, performance analysis, and isolation verification.
 * <p>
 * Implementation Note:
 * Baggage propagation is configured via application.properties:
 * - management.tracing.baggage.remote-fields=tenant-id
 * - management.tracing.baggage.correlation.fields=tenant-id
 * <p>
 * This makes tenant-id automatically:
 * - Propagated in HTTP headers (baggage-tenant-id)
 * - Added to MDC for logging correlation
 * - Included in trace exports
 *
 * @see com.onlineStoreCom.security.tenant.TenantContextFilter for where tenant-id is set into baggage
 */
@Configuration
public class TracingConfig {
    // Baggage configuration is handled via application.properties
    // No programmatic bean needed in Spring Boot 3.x
}

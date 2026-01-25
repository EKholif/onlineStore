package com.onlineStore.admin.security.tenant;

import com.onlineStoreCom.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TenantFilterAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantFilterAspect.class);

    @PersistenceContext
    private EntityManager entityManager;

    // Intercept ANY public method in ANY Service class (Matches *Service class name
    // suffix)
    @Before("execution(* com.onlineStore..*Service.*(..))")
    public void enforceTenantFilter() {
        Long tenantId = TenantContext.getTenantId();

        if (tenantId != null) {
            // AG-FIX-PLATFORM-ADMIN-002: Platform Admin (Tenant 0) should see ALL data
            // Do NOT apply tenant filter for Tenant 0 - they have cross-tenant visibility
            // [STRICT ENFORCEMENT] CONSTANT FILTERING
            // AG-FIX-PLATFORM-ADMIN-002: Root Tenant (0) is NO LONGER a super-user.
            // They must face the same data isolation as everyone else to prevent leaks.
            // Access to other tenants must be explicit via Impersonation (Switching
            // Context).

            // [STRICT ENFORCEMENT] For non-Platform tenants
            // We do NOT rely on the Web Filter alone. We force the Hibernate Filter
            // to be active on the current EntityManager Session right before execution.
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);

            // Only log at debug level to avoid spam, but this confirms it runs
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("🛡️ [TenantFilterAspect] Enforced Filter for TenantID: {}", tenantId);
            }
        } else {
            // [WARNING] Service called without TenantID.
            // This might be okay for login/shared data, but worth noting.
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("⚠️ [TenantFilterAspect] Service execution without TenantID");
            }
        }
    }
}

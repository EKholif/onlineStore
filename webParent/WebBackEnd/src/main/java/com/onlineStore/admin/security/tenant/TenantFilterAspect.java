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

    // Intercept ANY public method in ANY Service class (handling typo 'servcies')
    @Before("execution(* com.onlineStore.admin..service..*(..)) || execution(* com.onlineStore.admin..servcies..*(..))")
    public void enforceTenantFilter() {
        Long tenantId = TenantContext.getTenantId();

        if (tenantId != null) {
            // [STRICT ENFORCEMENT]
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

package com.onlineStore.admin.security.tenant;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Component
public class TenantFilterConfigurer {

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void enableTenantFilter() {
        Session session = entityManager.unwrap(Session.class);
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            session.enableFilter("tenantFilter")
                    .setParameter("tenantId", tenantId);
            System.out.println("Tenant filter enabled with tenantId: " + tenantId);
        }
    }
}

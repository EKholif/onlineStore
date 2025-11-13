package com.onlineStore.admin.security.tenant;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.LoadEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HibernateFilterConfigurer {

    @Autowired
    public HibernateFilterConfigurer(EntityManagerFactory emf) {
        SessionFactoryImplementor sessionFactory = emf.unwrap(SessionFactoryImplementor.class);

        EventListenerRegistry registry = sessionFactory
                .getServiceRegistry()
                .getService(EventListenerRegistry.class);

        registry.appendListeners(EventType.LOAD, (LoadEventListener) (event, loadType) -> {
            Session session = event.getSession().unwrap(Session.class);
            Long tenantId = TenantContext.getTenantId();

            if (tenantId != null && session.getEnabledFilter("tenantFilter") == null) {
                session.enableFilter("tenantFilter")
                        .setParameter("tenantId", tenantId);
            }
        });
    }
}

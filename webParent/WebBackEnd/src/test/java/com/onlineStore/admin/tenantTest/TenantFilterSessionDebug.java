package com.onlineStore.admin.tenantTest;

import jakarta.persistence.EntityManager;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class TenantFilterSessionDebug {

    @Autowired
    private EntityManager em;

    @Test
    public void printAllFilters() {
        Session session = em.unwrap(Session.class);

        // اطبع كل الفلاتر اللي موجودة على session ده

        Filter tenantFilter = session.getEnabledFilter("tenantFilter");
        if (tenantFilter != null) {
            System.out.println("Tenant filter is enabled, params: " + tenantFilter.getName());
        } else {
            System.out.println("Tenant filter is NOT enabled");
        }
        }
        }


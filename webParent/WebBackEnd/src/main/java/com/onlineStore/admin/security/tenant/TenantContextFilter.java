package com.onlineStore.admin.security.tenant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TenantContextFilter extends OncePerRequestFilter {

    private final EntityManagerFactory emf;

    public TenantContextFilter(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Long tenantId = TenantContext.getTenantId();

        // إنشاء EntityManager جديد لكل طلب
        EntityManager em = emf.createEntityManager();

        try {
            if (tenantId != null) {
                // تفعيل فلتر الـ tenant في Hibernate
                em.unwrap(Session.class)
                        .enableFilter("tenantFilter")
                        .setParameter("tenantId", tenantId);

                System.out.println("✅ Hibernate Tenant Filter Activated: " + tenantId);
            } else {
                System.out.println("⚠️ No tenantId found, all tenant queries will return empty");
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            em.close(); // مهم جداً تقفل الـ EntityManager بعد الطلب
        }
    }
}

package com.onlineStore.admin.test.counteryTest.userTest.tenant;

import com.onlineStore.admin.product.repository.ProductRepository;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class TenantFilterHttpSessionTest {

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testFilterWithSessionTenantId() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);

        Long tenantId = 8247009068765685744L;
        session.setAttribute("tenantId", tenantId);

        TenantContext.setTenantId((Long) session.getAttribute("tenantId"));

        Session hibSession = entityManager.unwrap(Session.class);
        hibSession.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        System.out.println("Tenant filter enabled from session: " + hibSession.getEnabledFilter("tenantFilter"));

        List<Product> products = productRepository.findAll();
        for (Product p : products) {
            System.out.println("Product tenantId: " + p.getTenantId() + " Name: " + p.getName());
            assertEquals(tenantId, p.getTenantId(), "Found product with wrong tenantId!");
        }

        TenantContext.clear();
    }
}

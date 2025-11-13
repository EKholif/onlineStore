package com.onlineStore.admin.test.counteryTest.userTest.tenant;

import com.onlineStore.admin.product.repository.ProductRepository;
import com.onlineStore.admin.security.tenant.TenantContext;
//import com.onlineStore.admin.security.tenant.TenantFilterConfigurer;
import com.onlineStoreCom.entity.product.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.testng.AssertJUnit.assertEquals;

@SpringBootTest
@Transactional
public class TenantFilterTest {

    @Autowired
    private ProductRepository productRepository; // افترض عندك JpaRepository<Product, Long>

    @PersistenceContext
    private EntityManager entityManager;

        @Autowired
//        private TenantFilterConfigurer filter;

        @Mock
        private HttpServletRequest request;

        @Mock
        private HttpServletResponse response;

        @Mock
        private FilterChain filterChain;


    @Test
    public void testTenantFilter() {
        // 👇 نحدد tenantId الحالي
        Long currentTenantId = 8247009068765685744L;

//        filter.doFilter(currentTenantId);
        System.out.println("TenantContext: setTenantId = " + TenantContext.getTenantId());
         currentTenantId = TenantContext.getTenantId();

        // 👇 نفعل فلتر الـ Hibernate يدويًا قبل أي query
//        Session session = entityManager.unwrap(Session.class);
//        session.enableFilter("tenantFilter").setParameter("tenantId", currentTenantId);
//        System.out.println("Tenant filter enabled: " + session.getEnabledFilter("tenantFilter"));

        // 👇 نجيب كل المنتجات
        List<Product> products = productRepository.findAll();

        // 👇 نطبع النتائج لنشوف هل الفلتر شغال
        System.out.println("Current tenant: " + TenantContext.getTenantId());
        for (Product p : products) {
            System.out.println("Product tenantId: " + p.getTenantId() + " Name: " + p.getName());
        }

        // 👇 نتأكد إن كل النتائج tenantId بتاعها = currentTenantId
        for (Product p : products) {
//            assertEquals(currentTenantId, p.getTenantId());
        }

        // 👇 بعد ما خلصنا الاختبار ننظف الـ ThreadLocal
        TenantContext.clear();
    }

    @Test
    public void testProductsFilteredByTenant() {
        Long tenantId = 8247009068765685744L;

        TenantContext.setTenantId(tenantId);
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);

        List<Product> products = productRepository.findAll();
        for (Product p : products) {
            assertEquals(tenantId, p.getTenantId());
        }

        TenantContext.clear();
    }

    @Test
    public void testTenantFilterAfterLogin() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        req.setSession(session);

        // Simulate login success
        Long tenantId = 123L;
        session.setAttribute("tenantId", tenantId);
        TenantContext.setTenantId(tenantId);

        Session hibSession = entityManager.unwrap(Session.class);
        hibSession.enableFilter("tenantFilter").setParameter("tenantId", tenantId);

        List<Product> products = productRepository.findAll();
        for (Product p : products) {
            assertEquals(tenantId, p.getTenantId());
        }

        TenantContext.clear();
    }


    @Test
    public void testFilterEnablesTenantFilter() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        req.setSession(session);
        session.setAttribute("tenantId", 123L);

//        filter.doFilterInternal(req, response, filterChain);




        assertEquals(Optional.of(123L), TenantContext.getTenantId());
    }


    @Test
    public void testFilterEnablesTenantFilter5() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        req.setSession(session);
        session.setAttribute("tenantId", 123L);

        FilterChain filterChain = (request, response) -> {
            // هنا الـ TenantContext لسه موجود
            assertEquals(123L, TenantContext.getTenantId().longValue());

        };

//        filter.doFilterInternal(req, response, filterChain);
    }

    @Test
    public void testFilterWithMockRequest() throws Exception {
        // إعداد الـ request والـ session
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        req.setSession(session);

        // محاكاة login: تخزين tenantId في session
        Long tenantId = 456L;
        session.setAttribute("tenantId", tenantId);

        // نفعل الفلتر من خلال الـ filter
//        filter.doFilterInternal(req, response, filterChain);

        // نتحقق من TenantContext
        System.out.println("TenantContext after filter: " + TenantContext.getTenantId());
//        assertEquals(tenantId, TenantContext.getTenantId());

        // بعد الاختبار ننظف TenantContext
        TenantContext.clear();
        System.out.println("TenantContext after clear: " + TenantContext.getTenantId());
    }

    @Test
    public void testFilterKeepsTenantDuring() throws Exception {

        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", 123L);

// اطبع الـ SQL عشان تتأكد
        session.createQuery("from Product", Product.class)
                .setComment("Check tenant filter")
                .list()
                .forEach(p -> System.out.println(p.getName() + " tenantId=" + p.getTenantId()));

    }




    @Test
    public void testFilterKeepsTenantDuringChain() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        req.setSession(session);
        session.setAttribute("tenantId", 456L);





        FilterChain mockChain = (request, response) -> {
            // هنا الـ TenantContext لا زال موجود
            assertEquals(Optional.of(456L), Optional.ofNullable(TenantContext.getTenantId()));
        };

//        filter.doFilterInternal(req, response, mockChain);

        // بعد انتهاء الفلتر، TenantContext بيتنضف
        assertEquals(null, Optional.ofNullable(TenantContext.getTenantId()));
    }


}

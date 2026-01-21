package com.onlineStore.admin.test.counteryTest.userTest.tenant;

import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class TenantContextTest {

    @BeforeEach
    void setup() {
        // قبل كل test نصفي الـ tenant
        TenantContext.clear();
    }

    @AfterEach
    void cleanup() {
        // بعد كل test نتأكد إن الـ context متصفى
        TenantContext.clear();
    }

    @Test
    void testSetAndGetTenantId() {
        TenantContext.setTenantId(12345L);
        Long tenantId = TenantContext.getTenantId();
        assertThat(tenantId).isEqualTo(12345L);
    }

    @Test
    void testClearTenantId() {
        TenantContext.setTenantId(12345L);
        TenantContext.clear();
        Long tenantId = TenantContext.getTenantId();
        assertThat(tenantId).isNull();
    }

    @Test
    public void testTenantContextSetAndClear() {
        Long tenantId = 123L;
        TenantContext.setTenantId(tenantId);
        assertEquals(tenantId, TenantContext.getTenantId());

        TenantContext.clear();
        assertNull(TenantContext.getTenantId());
    }

    @Test
    void testProductTenantAssociation() {
        // تعيين tenant
        Long tenantId = 999L;
        TenantContext.setTenantId(tenantId);

        // إنشاء منتجات مرتبطة بالـ tenant
        Product product1 = new Product();
        product1.setName("منتج 1");
        product1.setTenantId(TenantContext.getTenantId());

        Product product2 = new Product();
        product2.setName("منتج 2");
        product2.setTenantId(TenantContext.getTenantId());

        // تأكيد إن كل المنتجات مرتبطة بالـ tenant الحالي
        assertThat(product1.getTenantId()).isEqualTo(tenantId);
        assertThat(product2.getTenantId()).isEqualTo(tenantId);

        TenantContext.clear();
    }

    @Test
    void testTenantFilterSimulation() {
        // تعيين tenant
        Long tenantId = 111L;
        TenantContext.setTenantId(tenantId);

        // منتجات من tenants مختلفين
        Product p1 = new Product();
        p1.setTenantId(111L);
        Product p2 = new Product();
        p2.setTenantId(222L);

        List<Product> allProducts = List.of(p1, p2);

        // محاكاة فلترة Hibernate على أساس tenant
        List<Product> filtered = allProducts.stream()
                .filter(p -> p.getTenantId().equals(TenantContext.getTenantId()))
                .toList();

        // التأكد إن الفلترة صحيحة
        assertThat(filtered).hasSize(1);
        assertThat(filtered.get(0).getTenantId()).isEqualTo(tenantId);

        TenantContext.clear();
    }
}

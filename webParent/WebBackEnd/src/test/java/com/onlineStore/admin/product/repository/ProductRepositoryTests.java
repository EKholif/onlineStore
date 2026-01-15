package com.onlineStore.admin.product.repository;

import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repo;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {
        TenantContext.setTenantId(1L);
    }

    @AfterEach
    public void teardown() {
        TenantContext.clear();
    }

    @Test
    public void testEnableProductAll() {
        Product p1 = new Product("Prod 1");
        p1.setAlias("prod-1");
        p1.setShortDescription("Short");
        p1.setFullDescription("Full");
        p1.setMainImage("img.png");
        p1.setEnabled(false);
        p1.setTenantId(1L);
        p1.setInStock(true);
        p1.setPrice(10);
        p1.setCost(5);
        p1.setCreatedTime(new java.util.Date());
        p1.setUpdatedTime(new java.util.Date());

        repo.save(p1);

        repo.enableProductAll();
        entityManager.flush();
        entityManager.clear();

        Product updated = repo.findByAlias("prod-1");
        assertThat(updated.isEnabled()).isTrue();
    }
}

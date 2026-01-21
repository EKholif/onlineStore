package com.onlineStore.admin.category;

import com.onlineStoreCom.entity.category.Category;
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
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository repo;

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
    public void testFixBrokenHierarchies() {
        // Create Parent in Tenant 1
        TenantContext.setTenantId(1L);
        Category parent = new Category("Parent Cat");
        parent.setAlias("parent-cat");
        parent.setEnabled(true);
        parent.setTenantId(1L);
        parent.setImage("default.png");
        Category savedParent = repo.save(parent);

        // Create Child in Tenant 2, but pointing to Parent in Tenant 1 (Broken
        // Hierarchy)
        // We manipulate tenantId manually or via context switch
        TenantContext.setTenantId(2L);
        Category child = new Category("Child Cat");
        child.setAlias("child-cat");
        child.setEnabled(true);
        child.setTenantId(2L); // Manually ensure it is 2
        child.setImage("default.png");

        // Attaching parent from different tenant
        // To do this, we might need to bypass normal checks or just set it
        child.setParent(savedParent);

        Category savedChild = repo.save(child);

        assertThat(savedChild.getParent()).isNotNull();
        assertThat(savedChild.getParent().getId()).isEqualTo(savedParent.getId());

        // Now run fix
        // We need to run this likely without tenant filter or ensuring it covers
        // cross-tenant check
        // The JPQL is `UPDATE Category c SET c.parent = NULL WHERE c.parent.tenantId !=
        // c.tenantId`
        // This query interacts with multiple tenants.

        repo.fixBrokenHierarchies();
        entityManager.flush();
        entityManager.clear();

        Category updatedChild = repo.findById(savedChild.getId()).get();
        assertThat(updatedChild.getParent()).isNull();
    }
}

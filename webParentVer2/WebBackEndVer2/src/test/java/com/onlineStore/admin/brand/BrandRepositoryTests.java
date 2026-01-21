package com.onlineStore.admin.brand;

import com.onlineStore.admin.brand.reposetry.BrandRepository;
import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BrandRepositoryTests {

    @Autowired
    private BrandRepository repo;

    @BeforeEach
    public void setup() {
        TenantContext.setTenantId(1L);
    }

    @AfterEach
    public void teardown() {
        TenantContext.clear();
    }

    @Test
    public void testSearch() {
        // Create a brand to search for
        Brand brand = new Brand("TestGenericSearch");
        brand.setLogo("default.png");
        brand.setTenantId(1L);
        repo.save(brand);

        // Test search
        String keyword = "TestGenericSearch";
        Pageable pageable = PageRequest.of(0, 5);

        // This validates that BrandRepository successfully inherited
        // SearchRepository.findAll(String, Pageable)
        // and that the @Query implementation works
        Page<Brand> result = repo.findAll(keyword, pageable);

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getName()).isEqualTo("TestGenericSearch");

        // Cleanup
        repo.delete(brand);
    }

    @Test
    public void testSearchByCategory() {
        // Keep empty strictly as placeholder if needed or remove.
    }

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @Test
    public void testSearchByCategory_Real() {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        // Create Category
        Category category = new Category("Electronics" + uniqueId);
        category.setAlias("electronics-" + uniqueId);
        category.setEnabled(true);
        category.setTenantId(1L);
        category.setImage("default.png");
        entityManager.persist(category);

        // Create Brand linked to Category
        String brandName = "Samsung" + uniqueId;
        Brand brand = new Brand(brandName);
        brand.setLogo("samsung.png");
        brand.setTenantId(1L);
        brand.getCategories().add(category);
        repo.save(brand);

        // Create another brand NOT linked
        Brand brand2 = new Brand("Nike" + uniqueId);
        brand2.setLogo("nike.png");
        brand2.setTenantId(1L);
        repo.save(brand2);

        entityManager.flush();
        entityManager.clear(); // Ensure we fetch from DB

        // Test search with category filter
        String keyword = brandName;
        Pageable pageable = PageRequest.of(0, 5);

        // 1. Filter by correct category
        Page<Brand> result = repo.findAll(keyword, category.getId(), pageable);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo(brandName);

        // 2. Filter by same category, empty keyword (should still find it if we pass
        // "")
        result = repo.findAll("", category.getId(), pageable);
        // "Samsung" matches %""%? Yes.
        assertThat(result.getContent()).hasSize(1);

        // 3. Filter by wrong category (random ID)
        Page<Brand> emptyResult = repo.findAll(keyword, 9999, pageable);
        assertThat(emptyResult.getContent()).isEmpty();
    }
}

package com.onlineStore.admin.section;

import com.onlineStoreCom.entity.articals.Article;
import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.section.*;
import com.onlineStoreCom.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class SectionRepositoryTests {

    @Autowired
    private SectionRepository repo;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;
    @Autowired
    private com.onlineStore.admin.category.CategoryRepository categoryRepo;
    @Autowired
    private com.onlineStore.admin.product.repository.ProductRepository productRepo;
    @Autowired
    private com.onlineStore.admin.brand.reposetry.BrandRepository brandRepo;
    @Autowired
    private com.onlineStore.admin.article.ArticleRepository articleRepo;

    @AfterEach
    public void teardown() {
        TenantContext.clear();
    }

    @Test
    public void testAddAllCategoriesSection() {
        Section section = new Section();
        section.setHeading("Shopping by Categories Test");
        section.setDescription("Check out all categories...");
        section.setType(SectionType.ALL_CATEGORIES);
        section.setSectionOrder(2);
        section.setTenantId(1L);

        Section savedSection = repo.save(section);

        assertThat(savedSection).isNotNull();
        assertThat(savedSection.getId()).isGreaterThan(0);
    }

    @Test
    public void testAddCategorySection() {
        Section section = new Section();
        section.setHeading("Featured Categories Test");
        section.setDescription("Check out these featured categories...");
        section.setType(SectionType.CATEGORY);
        section.setSectionOrder(3);
        section.setTenantId(1L);

        for (int i = 1; i <= 3; i++) {
            CategorySection categorySection = new CategorySection();

            Category category = new Category();
            category.setName("Category " + i);
            category.setAlias("category-" + i);
            category.setEnabled(true);
            category.setTenantId(1L);
            category.setImage("default.png");
            Category savedCategory = categoryRepo.save(category);

            categorySection.setCategory(savedCategory);
            categorySection.setCategoryOrder(i);

            section.addCategorySection(categorySection);
        }

        Section savedSection = repo.save(section);

        assertThat(savedSection).isNotNull();
        assertThat(savedSection.getId()).isGreaterThan(0);
    }

    @Test
    public void testAddProductSection() {
        Section section = new Section();
        section.setHeading("Featured Products Test");
        section.setDescription("Check out these best-selling items...");
        section.setType(SectionType.PRODUCT);
        section.setSectionOrder(4);
        section.setTenantId(1L);

        Category category = new Category();
        category.setName("Product Category");
        category.setAlias("prod-cat");
        category.setEnabled(true);
        category.setTenantId(1L);
        category.setImage("default.png");
        Category savedCategory = categoryRepo.save(category);

        Brand brand = new Brand();
        brand.setName("Product Brand");
        brand.setLogo("default.png");
        brand.setTenantId(1L);
        Brand savedBrand = brandRepo.save(brand);

        for (int i = 1; i <= 3; i++) {
            ProductSection productSection = new ProductSection();

            Product product = new Product();
            product.setName("Product " + i);
            product.setAlias("product-" + i);
            product.setShortDescription("Short Desc");
            product.setFullDescription("Full Desc");
            product.setCreatedTime(new Date());
            product.setUpdatedTime(new Date());
            product.setEnabled(true);
            product.setInStock(true);
            product.setTenantId(1L);
            product.setCategory(savedCategory);
            product.setBrand(savedBrand);
            product.setPrice(100);
            product.setCost(50);
            product.setMainImage("default.png");

            Product savedProduct = productRepo.save(product);

            productSection.setProduct(savedProduct);
            productSection.setProductOrder(i);

            section.addProductSection(productSection);
        }

        Section savedSection = repo.save(section);

        assertThat(savedSection).isNotNull();
        assertThat(savedSection.getId()).isGreaterThan(0);
    }

    @Test
    public void testAddBrandSection() {
        Section section = new Section();
        section.setHeading("Featured Brands Test");
        section.setDescription("Recommended brands for shopping...");
        section.setType(SectionType.BRAND);
        section.setSectionOrder(5);
        section.setTenantId(1L);

        for (int i = 1; i <= 3; i++) {
            BrandSection brandSection = new BrandSection();
            Brand brand = new Brand();
            brand.setName("Brand " + i);
            brand.setLogo("default.png");
            brand.setTenantId(1L);
            Brand savedBrand = brandRepo.save(brand);

            brandSection.setBrandOrder(i);
            brandSection.setBrand(savedBrand);

            section.addBrandSection(brandSection);
        }

        Section savedSection = repo.save(section);

        assertThat(savedSection).isNotNull();
        assertThat(savedSection.getId()).isGreaterThan(0);
    }

    @Test
    public void testAddArticleSection() {
        Section section = new Section();
        section.setHeading("Shopping Tips Test");
        section.setDescription("Read these articles before shopping...");
        section.setType(SectionType.ARTICLE);
        section.setSectionOrder(6);
        section.setTenantId(1L);

        for (int i = 1; i <= 3; i++) {
            ArticleSection articleSection = new ArticleSection();

            Article article = new Article();
            article.setTitle("Section Article " + i);
            article.setContent("Content");
            article.setAlias("section-article-" + i);
            article.setPublished(true);
            article.setTenantId(1L);
            Article savedArticle = articleRepo.save(article);

            articleSection.setArticle(savedArticle);
            articleSection.setArticleOrder(i);

            section.addArticleSection(articleSection);
        }

        Section savedSection = repo.save(section);

        assertThat(savedSection).isNotNull();
        assertThat(savedSection.getId()).isGreaterThan(0);
    }

    @Test
    public void testDeleteSection() {
        Section section = new Section();
        section.setHeading("To Delete");
        section.setDescription("Desc");
        section.setType(SectionType.TEXT);
        section.setSectionOrder(1);
        section.setTenantId(1L);
        Section saved = repo.save(section);
        Integer sectionId = saved.getId();

        repo.deleteById(sectionId);
        entityManager.flush();
        entityManager.clear();
        Optional<Section> findById = repo.findById(sectionId);

        assertThat(findById).isNotPresent();
    }

    @Test
    public void testDisableSection() {
        Section section = new Section();
        section.setHeading("To Disable");
        section.setDescription("Desc");
        section.setType(SectionType.TEXT);
        section.setSectionOrder(1);
        section.setTenantId(1L);
        section.setEnabled(true);
        Section saved = repo.save(section);
        Integer sectionId = saved.getId();

        repo.updateEnabledStatus(sectionId, false);
        entityManager.clear();
        Section updated = repo.findById(sectionId).get();

        assertThat(updated.isEnabled()).isFalse();
    }

    @Test
    public void testEnableSection() {
        Section section = new Section();
        section.setHeading("To Enable");
        section.setDescription("Desc");
        section.setType(SectionType.TEXT);
        section.setSectionOrder(1);
        section.setTenantId(1L);
        section.setEnabled(false);
        Section saved = repo.save(section);
        Integer sectionId = saved.getId();

        repo.updateEnabledStatus(sectionId, true);
        entityManager.clear();
        Section updated = repo.findById(sectionId).get();

        assertThat(updated.isEnabled()).isTrue();
    }

    @BeforeEach
    public void setup() {
        TenantContext.setTenantId(1L);
        try {
            entityManager.createNativeQuery("DELETE FROM sections_products").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM sections_categories").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM sections_brands").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM sections_articles").executeUpdate();

            entityManager.createNativeQuery("DELETE FROM sections").executeUpdate();
            entityManager.flush();
            entityManager.createNativeQuery("ALTER TABLE sections DROP INDEX UK_i816mxaoddmtveofp1qjbnngf")
                    .executeUpdate();
        } catch (Exception e) {
            System.out.println("Cleanup warning (some tables might be empty or index missing): " + e.getMessage());
        }
    }

    @Test
    public void testAddTextSection() {
        Section section = new Section();
        section.setHeading("Annoucement Test");
        section.setDescription("Shop Annoucement in Summer 2022: The great sales season is coming...");
        section.setType(SectionType.TEXT);
        section.setSectionOrder(1);
        section.setTenantId(1L);

        Section savedSection = repo.save(section);

        assertThat(savedSection).isNotNull();
        assertThat(savedSection.getId()).isGreaterThan(0);
    }

    @Test
    public void testListSectionsNotSorted() {
        Section section = new Section();
        section.setHeading("Unsorted Section Test");
        section.setDescription("Description");
        section.setType(SectionType.TEXT);
        section.setSectionOrder(1);
        section.setTenantId(1L);
        repo.save(section);

        List<Section> sections = repo.findAll();
        assertThat(sections).isNotEmpty();

        sections.forEach(System.out::println);
    }

    @Test
    public void testListSectionsSorted() {
        Section section1 = new Section();
        section1.setHeading("Section 1");
        section1.setDescription("Desc 1");
        section1.setType(SectionType.TEXT);
        section1.setSectionOrder(1);
        section1.setTenantId(1L);
        repo.save(section1);

        Section section2 = new Section();
        section2.setHeading("Section 2");
        section2.setDescription("Desc 2");
        section2.setType(SectionType.TEXT);
        section2.setSectionOrder(2);
        section2.setTenantId(1L);
        repo.save(section2);

        List<Section> sections = repo.findAllSectionsSortedByOrder();

        assertThat(sections).isNotEmpty();
        sections.forEach(System.out::println);
    }

    @Test
    public void testGetSimpleSectionById() {
        Section section = new Section();
        section.setHeading("Simple Section");
        section.setDescription("Simple Description");
        section.setType(SectionType.TEXT);
        section.setSectionOrder(1);
        section.setTenantId(1L);
        Section saved = repo.save(section);

        Section retrieved = repo.getSimpleSectionById(saved.getId());

        assertThat(retrieved).isNotNull();
    }

    @Test
    public void testGetSectionIDsSortedByOrder() {
        Section section = new Section();
        section.setHeading("Section for IDs");
        section.setDescription("Desc");
        section.setType(SectionType.TEXT);
        section.setSectionOrder(1);
        section.setTenantId(1L);
        repo.save(section);

        List<Section> sections = repo.getOnlySectionIDsSortedByOrder();
        assertThat(sections).isNotEmpty();

        sections.forEach(System.out::println);
    }

    @Test
    public void testUpdateSectionPosition() {
        Section section = new Section();
        section.setHeading("To Update Position");
        section.setDescription("Desc");
        section.setType(SectionType.TEXT);
        section.setSectionOrder(1);
        section.setTenantId(1L);
        Section saved = repo.save(section);
        Integer sectionId = saved.getId();

        Integer newOrder = 2;

        repo.updateSectionPosition(newOrder, sectionId);
        entityManager.clear();

        Section updated = repo.findById(sectionId).get();
        assertThat(updated.getSectionOrder()).isEqualTo(newOrder);
    }
}

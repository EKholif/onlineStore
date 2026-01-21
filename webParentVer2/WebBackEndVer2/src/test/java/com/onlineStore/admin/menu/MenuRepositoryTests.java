package com.onlineStore.admin.menu;

import com.onlineStoreCom.entity.articals.Article;
import com.onlineStoreCom.entity.menu.Menu;
import com.onlineStoreCom.entity.menu.MenuType;
import com.onlineStoreCom.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class MenuRepositoryTests {

    @Autowired
    private MenuRepository repo;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;
    @Autowired
    private com.onlineStore.admin.article.ArticleRepository articleRepo;

    @BeforeEach
    public void setup() {
        TenantContext.setTenantId(1L);
        try {
            entityManager.createNativeQuery("DELETE FROM menus").executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE menus DROP INDEX UK_7fcv9rm1ecfeumh1lo4tuntgm")
                    .executeUpdate();
        } catch (Exception e) {
            // Index might not exist or already dropped
        }
    }

    @AfterEach
    public void teardown() {
        TenantContext.clear();
    }

    @Test
    public void testCreateHeaderMenu() {
        Menu menu = new Menu();
        menu.setType(MenuType.HEADER);
        menu.setTitle("About online shop");
        menu.setAlias("about");
        menu.setEnabled(true);
        menu.setPosition(1);
        menu.setTenantId(1L);

        Article article = new Article();
        article.setTitle("About Article");
        article.setContent("Content");
        article.setAlias("about-article");
        article.setPublished(true);
        article.setTenantId(1L);
        Article savedArticle = articleRepo.save(article);

        menu.setArticle(savedArticle);

        Menu savedMenu = repo.save(menu);

        assertTrue(savedMenu.getId() > 0);
    }

    @Test
    public void testCreateFooterMenu() {
        Menu menu = new Menu();
        menu.setType(MenuType.FOOTER);
        menu.setTitle("Shipping");
        menu.setAlias("shipping");
        menu.setEnabled(false);
        menu.setPosition(2);
        menu.setTenantId(1L);

        Article article = new Article();
        article.setTitle("Shipping Article");
        article.setContent("Shipping Content");
        article.setAlias("shipping-article");
        article.setPublished(true);
        article.setTenantId(1L);
        Article savedArticle = articleRepo.save(article);

        menu.setArticle(savedArticle);

        Menu savedMenu = repo.save(menu);

        assertTrue(savedMenu.getId() > 0);
    }

    @Test
    public void testListMenuByTypeThenByPosition() {
        Menu menu = new Menu();
        menu.setType(MenuType.HEADER);
        menu.setTitle("Header Menu");
        menu.setAlias("header");
        menu.setPosition(1);
        menu.setTenantId(1L);
        menu.setEnabled(true);

        Article article = new Article();
        article.setTitle("Header Article");
        article.setContent("Content");
        article.setAlias("header-article");
        article.setPublished(true);
        article.setTenantId(1L);
        Article savedArticle = articleRepo.save(article);

        menu.setArticle(savedArticle);
        repo.save(menu);

        List<Menu> listMenuItems = repo.findAllByOrderByTypeAscPositionAsc();
        assertThat(listMenuItems.size()).isGreaterThan(0);

        listMenuItems.forEach(System.out::println);
    }

    @Test
    public void testCountFooterMenus() {
        Menu menu = new Menu();
        menu.setType(MenuType.FOOTER);
        menu.setTitle("Footer Menu");
        menu.setAlias("footer");
        menu.setPosition(1);
        menu.setTenantId(1L);
        menu.setEnabled(true);

        Article article = new Article();
        article.setTitle("Footer Article");
        article.setContent("Content");
        article.setAlias("footer-article");
        article.setPublished(true);
        article.setTenantId(1L);
        Article savedArticle = articleRepo.save(article);

        menu.setArticle(savedArticle);
        repo.save(menu);

        Long numberOfFooterMenus = repo.countByType(MenuType.FOOTER);
        assertEquals(1, numberOfFooterMenus);
    }

    @Test
    public void testDisableMenuItem() {
        Menu menu = new Menu();
        menu.setType(MenuType.HEADER);
        menu.setTitle("To Disable");
        menu.setAlias("disable");
        menu.setPosition(1);
        menu.setTenantId(1L);
        menu.setEnabled(true);

        Article article = new Article();
        article.setTitle("Disable Article");
        article.setContent("Content");
        article.setAlias("disable-article");
        article.setPublished(true);
        article.setTenantId(1L);
        Article savedArticle = articleRepo.save(article);

        menu.setArticle(savedArticle);
        Menu saved = repo.save(menu);
        Integer menuId = saved.getId();

        repo.updateEnabledStatus(menuId, false);
        entityManager.clear();
        Menu updatedMenu = repo.findById(menuId).get();

        assertFalse(updatedMenu.isEnabled());
    }

    @Test
    public void testEnableMenuItem() {
        Menu menu = new Menu();
        menu.setType(MenuType.HEADER);
        menu.setTitle("To Enable");
        menu.setAlias("enable");
        menu.setPosition(1);
        menu.setTenantId(1L);
        menu.setEnabled(false);

        Article article = new Article();
        article.setTitle("Enable Article");
        article.setContent("Content");
        article.setAlias("enable-article");
        article.setPublished(true);
        article.setTenantId(1L);
        Article savedArticle = articleRepo.save(article);

        menu.setArticle(savedArticle);
        Menu saved = repo.save(menu);
        Integer menuId = saved.getId();

        repo.updateEnabledStatus(menuId, true);
        entityManager.clear();
        Menu updatedMenu = repo.findById(menuId).get();

        assertTrue(updatedMenu.isEnabled());
    }

    @Test
    public void testListHeaderMenuItems() {
        Menu menu = new Menu();
        menu.setType(MenuType.HEADER);
        menu.setTitle("Header Item");
        menu.setAlias("header-item");
        menu.setPosition(1);
        menu.setTenantId(1L);
        menu.setEnabled(true);

        Article article = new Article();
        article.setTitle("Header List Article");
        article.setContent("Content");
        article.setAlias("header-list-article");
        article.setPublished(true);
        article.setTenantId(1L);
        Article savedArticle = articleRepo.save(article);

        menu.setArticle(savedArticle);
        repo.save(menu);

        List<Menu> listHeaderMenuItems = repo.findByTypeOrderByPositionAsc(MenuType.HEADER);
        assertThat(listHeaderMenuItems).isNotEmpty();
    }

    @Test
    public void testListFooterMenuItems() {
        Menu menu = new Menu();
        menu.setType(MenuType.FOOTER);
        menu.setTitle("Footer Item");
        menu.setAlias("footer-item");
        menu.setPosition(1);
        menu.setTenantId(1L);
        menu.setEnabled(true);

        Article article = new Article();
        article.setTitle("Footer List Article");
        article.setContent("Content");
        article.setAlias("footer-list-article");
        article.setPublished(true);
        article.setTenantId(1L);
        Article savedArticle = articleRepo.save(article);

        menu.setArticle(savedArticle);
        repo.save(menu);

        List<Menu> listFooterMenuItems = repo.findByTypeOrderByPositionAsc(MenuType.FOOTER);
        assertThat(listFooterMenuItems).isNotEmpty();
    }
}

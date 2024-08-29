package com.example.frontend;

import com.example.frontend.category.CategoryRepository;
import com.example.frontend.product.*;
import com.onlineStoreCom.entity.category.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTests {

    @Autowired private CategoryRepository repo;
    @Autowired private ProductRepository productRepository;
    @Test
    public void testListEnabledCategories() {
        List<Category> categories = repo.findAllEnabled();
        categories.forEach(category -> {
            System.out.println(category.getName() + " (" + category.isEnable() + ")");
        });
    }

    @Test
    public void testFindCategoryByAlias() {

        String alias = "electronics";
        String aliasProduct = "computer_sound_cards";

        Category category = repo.findByAliasEnabled(alias);




//        productRepository.ge


        assertThat(category).isNotNull();
    }


}

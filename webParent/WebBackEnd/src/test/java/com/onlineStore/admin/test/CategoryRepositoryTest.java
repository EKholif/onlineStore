package com.onlineStore.admin.test;


import com.onlineStore.admin.category.CategoryRepository;
import com.onlineStoreCom.entity.prodact.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repository;



    @Test
    public void testCreatRootCategory(){

        Category category1 = new Category("Computers");
        Category category = new Category("Electronics");
        Category      save = repository.saveAndFlush(category1);
        Category      save1 = repository.saveAndFlush(category);
        assertThat(save.getId()).isGreaterThan(0);
    }


    @Test
    public void testACreatRootCategory(){

        Category category = new Category("Computers");

        Category      save = repository.saveAndFlush(category);
        assertThat(save.getId()).isGreaterThan(0);
    }


    @Test
    public void testCreatSubCategory(){

        Category parent = new Category(1L);
        Category     subCategory = new Category("LapTop",parent);
        Category      save = repository.saveAndFlush(subCategory);

        assertThat(save.getId()).isGreaterThan(0);
    }
    @Test
    public void test3CreatSubCategory(){

        Category parent = new Category(5L);
        Category     subCategory = new Category("Hard Desk",parent);
        Category      save = repository.saveAndFlush(subCategory);

        assertThat(save.getId()).isGreaterThan(0);
    }
    @Test
    public void testCreatSubCategoryList(){

        Category parent =  new  Category(1L);
        Category     deskTop = new Category("Desk Top",parent);
        Category  components    = new Category("Computer Components",parent);
         repository.saveAllAndFlush(List.of(deskTop,components));

    }


    @Test
    public void testCreatSubList(){

        Category parent =  new  Category(2L);
        Category     deskTop = new Category("Camera",parent);
        Category  components    = new Category("SmartPhones ",parent);
        repository.saveAllAndFlush(List.of(deskTop,components));

    }

    @Test
    public void test2CreatSubCategoryList(){

        Category parent =  new  Category(9L);
        Category camera = new Category("Apple",parent);
        Category smartPhone    = new Category("SamSung",parent);
        repository.saveAllAndFlush(List.of(camera,smartPhone));

    }



    @Test
    public void test3CreatSubSubCategory(){

        Category parent = new Category(48L);
        Category     ssubCategory = new Category("Sujjjjjjper ",parent);
        Category      save = repository.saveAndFlush(ssubCategory);

        assertThat(save.getId()).isGreaterThan(0);

        testGetAllCategory();


    }
    @Test
    public void testGetCategory() {

        Category category = repository.getReferenceById(1L);


        System.out.println(category.getParent().getName());

        Set<Category> children = category.getChildren();
        for (Category subCategory : children){

            System.out.println(subCategory.getName());

        }
        assertThat(children.size()).isGreaterThan(0);
    }

//

    @Test
    public void testEnable() {

//        category.setEnable(false);
        System.out.println(repository.count());


    }

    @Test
    public void testGetAllCategory() {
        Iterable<Category> categories = repository.findAll();

        for (Category category : categories) {
//            if (category.getParent() == null  ) {
                printCategoryHierarchy(category, category.getLevel());

//            }

//            if ( category.getParent() != null && category.getParent().getId()==category.getId()  ){
////                printCategoryHierarchy(category, 0);
////                System.out.println(category.getId());
//                System.out.println("dddddddddddddddddddddd");
//            }

        }
    }

    private void printCategoryHierarchy(Category category, int depth) {
        // Print the current category with indentation based on the depth
        String indentation = "   ".repeat(depth);
        System.out.println(indentation + "**" + category.getId() + " -" + category.getName());

        // Recursively print subcategories
//        Set<Category> children = category.getChildren();
//        for (Category subCategory : children) {
//            printCategoryHierarchy(subCategory, depth + 1);
//        }
    }


    @Test
    public void getCategoriesLevel(){

        List<Category> root = repository.findAll();

        for ( Category cat   : root  ) {

            System.out.println(cat.getName() +"  parent Id :    " + cat.parentIndentation());


        }


        }

            @Test
  public void setCategoriesLevel(){

        List<Category> root = repository.findAll();

        for ( Category cat   : root  ) {


            if (cat.getParent() == null) {
                cat.setLevel(0) ;
                System.out.println( "No parent "+cat);
            } else {

                cat.setLevel( cat.getParent().getLevel() + 1);
                System.out.println(cat.getParent().getLevel());
                System.out.println( "Cat Name "+cat.getName());

            }

        }

    }



}

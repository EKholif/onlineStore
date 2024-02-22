package com.onlineStore.admin.test;


import com.onlineStore.admin.category.CategoryRepository;
import com.onlineStoreCom.entity.prodact.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;

import java.util.*;

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
    public void testgetPage() {

//        testGetAllCategory();


       Iterable <Category> categoriesUsedInForm =  repository.findAll();

        List<Category> man = null;

        for (Category cat: categoriesUsedInForm) {

            man.add(cat);
        }


      Pageable pageable = PageRequest.of(3, 4);


        System.out.println( new PageImpl<>(man, pageable, man.size()));
    }



//    public Page<Category> listByPage(int pageNum, String sortField, String sortDir,
//                                     String keyWord) {
//
//        if(sortField .equals("parent")){ sortField= "parent.level";}
//
//
//        Sort sort = Sort.by(sortField);
//
//        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
//
//        Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
//
//        if (keyWord != null) {
//
//            return repository.findAll(keyWord, pageable);
//        }
//        return getPage(listUsedForForm(),pageable);
//
////        return repository.findAll(pageable);
//    }





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

    @Test
    public void findCategoryByName(){

        String name = "Electronics";

        Sort sort = Sort.by("name");

        sort =  sort.ascending() ;

        Pageable pageable = PageRequest.of(0 , 4);

        Page<Category> cat = repository.findAll(name,pageable);

        for (Category c: cat ) {
            System.out.println("ehab");
            System.out.println(c.getName());
        }

    }
    @Test
    public void findCategoryByAlias(){

        String alias = "BB";

        Category cat = repository.findByAlias(alias);

        System.out.println(cat.getId());
    }

    @Test
    public void testGetAllCategory() {

        String sortDir="asc";

        List<Category> categoriesUsedInForm = new ArrayList<>();
        Iterable<Category> categories = repository.findAll();
        List<Category> topLevelCategories = new ArrayList<>();

        for (Category category : categories) {
            if (category.getParent() == null  ) {
                topLevelCategories.add(category);

            }

            if ( category.getParent() != null && category.getParent().getId()==category.getId()  ){
                topLevelCategories.add(category);
            }

        }
        Collections.sort(topLevelCategories, Comparator.comparing(Category::getId));

// Print the sorted top-level categories
        for (Category category : topLevelCategories) {
            printCategoryHierarchy(category, categoriesUsedInForm,category.getLevel());
        }
    }


    private void printCategoryHierarchy(Category category,List<Category> categoriesUsedInForm, int depth) {
        // Print the current category with indentation based on the depth

        List<Category> topLevelCategories = new ArrayList<>();
        String indentation = "*".repeat(depth);
        topLevelCategories.add(new Category(indentation  + category.getId() + "-" + category.getName()));

//        getPage( topLevelCategories,1,4);




        Set<Category> children = category.getChildren();
        for (Category subCategory : children) {
            printCategoryHierarchy(subCategory, categoriesUsedInForm,depth + 1);
        }

return  ;
    }


          private void getPage(List<Category> categoriesUsedInForm, int pageNum, int USERS_PER_PAGE) {
              // Create a pageable object for the specified page number and page size
              Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE);
    
              // Calculate the range of categories for the specified page
              int start = (int) pageable.getOffset();
              int end = Math.min(start + pageable.getPageSize(), categoriesUsedInForm.size());
    

              // Extract the categories for the specified page
              List<Category> pageCategories = categoriesUsedInForm.subList(start, end);
    
              // Print the content of the page
              for (Category category : pageCategories) {
                  System.out.println(category.getName());
              }
          }


          

    private void printLnCategory(Category category,int depth ){
        String indentation = " ".repeat(depth);
        System.out.println(indentation + "*" + category.getId() + "-" + category.getName());
    }
}
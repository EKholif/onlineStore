package com.onlineStore.admin.category.services;

import com.onlineStore.admin.UsernameNotFoundException;
import com.onlineStore.admin.category.CategoryRepository;
import com.onlineStoreCom.entity.prodact.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CategoryService {

    public static final int USERS_PER_PAGE=4;


    @Autowired
    private CategoryRepository repository;

    public List<Category> listAll(){return repository.findAll();

    }
    public Category getCategory(Long id) throws UsernameNotFoundException {
        try {
            return repository.getReferenceById(id);

        }catch (NoSuchElementException ex){

            throw new UsernameNotFoundException("Could not find any Category with ID " + id);
        }
    }
//    public List<Category> listUsedForForm() {
//        List<Category> categoriesUsedInForm = new ArrayList<>();
//
//        Iterable<Category> categories = repository.findAll();
//
//        for (Category category : categories) {
//            if (category.getParent() == null) {
//                printCategoryHierarchy(category, categoriesUsedInForm, 0);
//            }
//        }
//
//        return categoriesUsedInForm;
//    }
//
//
//    private void printCategoryHierarchy(Category category, List<Category> categoriesUsedInForm, int level) {
//        // Add the current category to the list with id
//        categoriesUsedInForm.add(new Category(category.getId(), getIndentation(level) + category.getName()));
//
//        // Recursively add subcategories to the list
//        Set<Category> children = category.getChildren();
//        for (Category subCategory : children) {
//            printCategoryHierarchy(subCategory, categoriesUsedInForm, level + 1);
//        }
//    }
//
//    private String getIndentation(int level) {
//        // You can customize the indentation character(s) and size based on your preference
//        StringBuilder indentation = new StringBuilder();
//        for (int i = 0; i < level; i++) {
//            indentation.append("-"); // Two spaces for each level; adjust as needed
//        }
//        return indentation.toString();
//    }
    public List<Category> listUsedForForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();

        Iterable<Category> categories = repository.findAll();

        for (Category category : categories) {

                printCategoryHierarchy(category, categoriesUsedInForm, category.getLevel());
        }

        return categoriesUsedInForm;
    }


    private void printCategoryHierarchy(Category category, List<Category> categoriesUsedInForm, int level) {
        // Add the current category to the list with id
        categoriesUsedInForm.add(new Category(category.getId(), getIndentation(level) + category.getName()));

    }

    private String getIndentation(int level) {
        // You can customize the indentation character(s) and size based on your preference
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indentation.append("-"); // Two spaces for each level; adjust as needed
        }
        return indentation.toString();
    }
    public Page<Category> listByPage(int pageNum, String sortField, String sortDir,
                                  String keyWord) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

        if (keyWord != null) {

            return repository.findAll(keyWord, pageable);
        }
        return repository.findAll(pageable);
    }

    public Category saveCategory (Category category) {
        return repository.saveAndFlush(category);

    }


    public void deleteCategory(Long id) throws UsernameNotFoundException{
        try {
            repository.deleteById(id);

        }catch (NoSuchElementException ex){

            throw new UsernameNotFoundException("Could not find any Category with ID " + id);
        }
    }

    public void UpdateCategoryEnableStatus (Long id, Boolean enable){
        repository.enableCategory(id, enable);

    }
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

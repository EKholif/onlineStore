package com.onlineStore.admin.category.services;

import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.category.CategoryRepository;
import com.onlineStoreCom.entity.prodact.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class CategoryService {

    public static final int USERS_PER_PAGE=4;


    @Autowired
    private CategoryRepository repository;

    public List<Category> listAll(){return repository.findAll();}

    public Category getCategory(Long id) throws CategoryNotFoundException {
        try {

            return repository.findById(id).get();


        }catch (NoSuchElementException ex){


            throw new CategoryNotFoundException("Could not find any Category with ID " + id);
        }
    }



    public String checkUnique(Long id, String name, String alias){

//        boolean isCreatingNew = (id==null|| id==0L);

          Category categoryByName = repository.findByName(name);
          Category categoryByAlias = repository.findByAlias(alias);

//        if (isCreatingNew){

               if (categoryByName != null && categoryByName.getId() !=id ){
                     return "DuplicateName";}

//               }else {

                if (categoryByAlias != null  && categoryByAlias.getId() !=id ){
                    return "DuplicateAlies";}
//                  }

//                if (categoryByAlias != null && categoryByAlias.getId() !=id){
//                    return "DuplicateName";}
//
//        }
            return "Ok";

    }


    public List<Category> listUsedForForm() {

        String sortField = "name";
        String sortDir="asc";

        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();


        List<Category> categoriesUsedInForm = new ArrayList<>();

        Iterable<Category> categories = repository.findAll( sort);

        for (Category category : categories) {
            if (category.getParent() == null  ) {
                printCategoryHierarchy(category, categoriesUsedInForm,category.getLevel());

            }

            if ( category.getParent() != null && category.getParent().getId()==category.getId()  ){
                printCategoryHierarchy(category, categoriesUsedInForm,category.getLevel());
            }
        }
        return categoriesUsedInForm;
    }



    private void printCategoryHierarchy(Category category,List<Category> categoriesUsedInForm, int depth) {
        // Print the current category with indentation based on the depth


        String indentation = "*".repeat(depth);
        categoriesUsedInForm.add(new Category(indentation  + category.getId() + "-" + category.getName()));

        // Recursively print subcategories
        Set<Category> children = category.getChildren();
        for (Category subCategory : children) {
            printCategoryHierarchy(subCategory, categoriesUsedInForm,depth + 1);
        }
    }


    public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortField, String sortDir,
                                       String keyWord ) {

        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

        List<Category> categoriesUsedInForm = new ArrayList<>();

        Page<Category> pageCategories = null;

        if (keyWord != null && !keyWord.isEmpty()) {

            pageCategories = repository.findAll(keyWord, pageable);

             List<Category> categories = pageCategories.getContent();


             return categories;

        } else {

            pageCategories = repository.findAll(pageable);

            pageInfo.setTotalElements(pageCategories.getTotalElements());
            pageInfo.setTotalPages(pageCategories.getTotalPages());
            List<Category> categories = pageCategories.getContent();
            return categories;

        }

//
////        pageInfo.setTotalElements(pageCategories.getTotalElements());
////        pageInfo.setTotalPages(pageCategories.getTotalPages());
//
//        List<Category> categories = pageCategories.getContent();
//
//            for (Category category : categories) {
//
//            if (category.getParent() == null  ) {
//                printCategoryHierarchy(category, categoriesUsedInForm,category.getLevel());
//
//            }
//
//            if ( category.getParent() != null && category.getParent().getId()==category.getId()  ){
//                printCategoryHierarchy(category, categoriesUsedInForm,category.getLevel());
//
//            }
//        }
//
//        return categoriesUsedInForm;
    }



    public Page<Category> listByPage1(int pageNum, String sortField, String sortDir,
                String keyWord) {


            Sort sort = Sort.by(sortField);

            sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

            Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);




        if (keyWord != null) {

            return  repository.findAll(keyWord,pageable );
        }

        return  repository.findAll(pageable );
    }






    public Category saveCategory (Category category) {
        setCategoryLevel(category);
        return repository.saveAndFlush(category);

    }


    public void setCategoryLevel (Category category) {

            if(category.getParent()==null )
            {
             category.setLevel(0);
            } else {

                category.setLevel(category.getParent().getLevel()+1) ;
            }


        repository.saveAndFlush(category);

    }

    public void deleteCategory(Long id) throws CategoryNotFoundException{
        try {
            repository.deleteById(id);

        }catch (NoSuchElementException ex){

            throw new CategoryNotFoundException("Could not find any Category with ID " + id);
        }
    }

    public void UpdateCategoryEnableStatus (Long id, Boolean enable){
        repository.enableCategory(id, enable);

    }



    }



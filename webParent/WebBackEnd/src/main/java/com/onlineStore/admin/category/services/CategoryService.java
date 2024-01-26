package com.onlineStore.admin.category.services;

import com.onlineStore.admin.category.CategoryNotFoundException;
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



package com.onlineStore.admin.category.services;

import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.category.CategoryRepository;
import com.onlineStoreCom.entity.category.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class CategoryService {

    public static final int USERS_PER_PAGE = 4;


    @Autowired
    private CategoryRepository categoryRepo;

    public List<Category> listAll() {
        return categoryRepo.findAll();
    }

    public Category findById(Integer id) throws CategoryNotFoundException {
        try {


            return categoryRepo.findById(id).get();


        } catch (NoSuchElementException ex) {


            throw new CategoryNotFoundException("Could not find any Category with ID " + id);
        }
    }

    public Boolean existsById(Integer id) {
        return categoryRepo.findById(id).isPresent();
    }


    public String checkUnique(Integer id, String name, String alias) {

//        boolean isCreatingNew = (id==null|| id==0L);

        Category categoryByName = categoryRepo.findByName(name);
        Category categoryByAlias = categoryRepo.findByAlias(alias);

//        if (isCreatingNew){

        if (categoryByName != null && categoryByName.getId() != id) {
            return "DuplicateName";
        }

//               }else {

        if (categoryByAlias != null && categoryByAlias.getId() != id) {
            return "DuplicateAlies";
        }
//                  }

//                if (categoryByAlias != null && categoryByAlias.getId() !=id){
//                    return "DuplicateName";}
//
//        }
        return "Ok";

    }


    public List<Category> listUsedForForm() {

        String sortField = "name";

        String sortDir = "asc";

        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();


        List<Category> categoriesUsedInForm = new ArrayList<>();

        Iterable<Category> categories = categoryRepo.findAll(sort);

        for (Category category : categories) {
            if (category.getParent() == null) {
                printCategoryHierarchy(category, categoriesUsedInForm, category.getLevel(), sortDir);

            }

            if (category.getParent() != null && category.getParent().getId() == category.getId()) {
                printCategoryHierarchy(category, categoriesUsedInForm, category.getLevel(), sortDir);
            }
        }


        return categoriesUsedInForm;
    }


    private void printCategoryHierarchy(Category category, List<Category> categoriesUsedInForm, int depth, String sortDir) {

        String indentation = "*".repeat(depth) + category.getId() + "-" + category.getName();
        categoriesUsedInForm.add(Category.copyFull(category, indentation));

        Set<Category> children = sortSubCategories(category.getChildren(), sortDir);
        for (Category subCategory : children) {
            printCategoryHierarchy(subCategory, categoriesUsedInForm, depth + 1, sortDir);
        }
    }


    public List<Category> listByPage(PageInfo pageInfo, int pageNum, String sortField, String sortDir, String keyWord) {


        if (sortField == null || sortDir.isEmpty()) {
            sortField = "name";
        }
        Sort sort = Sort.by(sortField);


        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

        Page<Category> pageCategories = null;

        if (keyWord != null && !keyWord.isEmpty()) {

            pageCategories = categoryRepo.findAll(keyWord, pageable);

        } else {
            pageCategories = categoryRepo.findRootCategories(pageable);
        }

        List<Category> searchResult = pageCategories.getContent();
        List<Category> rootCategories = pageCategories.getContent();

        pageInfo.setTotalElements(pageCategories.getTotalElements());
        pageInfo.setTotalPages(pageCategories.getTotalPages());

        List<Category> categoriesUsedInForm = new ArrayList<>();

        if (keyWord != null && !keyWord.isEmpty()) {


            for (Category category : searchResult) {
                category.setHasChildren(!category.getChildren().isEmpty());
            }

            // Todo : fix listHierarchicalCategories to Category return by Page

            return listHierarchicalCategories(searchResult, sortDir);

        } else {


            for (Category category : rootCategories) {

                printCategoryHierarchy(category, categoriesUsedInForm, category.getLevel(), sortDir);
            }

// Todo : fix categoriesUsedInForm to Category return by Page


            pageInfo.setTotalPages(categoriesUsedInForm.size() / USERS_PER_PAGE);
            pageInfo.setTotalElements(categoriesUsedInForm.size());
            return categoriesUsedInForm;
        }

    }


    private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category rootCategory : rootCategories) {
            hierarchicalCategories.add(Category.copyFull(rootCategory));

            Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);

            for (Category subCategory : children) {
                String name = "--" + subCategory.getName();

                hierarchicalCategories.add(Category.copyFull(subCategory, name));

                listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
            }
        }


        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(List<Category> hierarchicalCategories,
                                               Category parent, int subLevel, String sortDir) {

        Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
        int newSubLevel = subLevel + 1;

        for (Category subCategory : children) {
            String name = "";
            for (int i = 0; i < newSubLevel; i++) {
                name += "--";
            }
            name += subCategory.getName();

            hierarchicalCategories.add(Category.copyFull(subCategory, name));

            listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
        }

    }

    private SortedSet<Category> sortSubCategories(Set<Category> children) {
        return sortSubCategories(children, "asc");
    }

    private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
        SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
            @Override
            public int compare(Category cat1, Category cat2) {
                if (sortDir.equals("asc")) {
                    return cat1.getName().compareTo(cat2.getName());
                } else {
                    return cat2.getName().compareTo(cat1.getName());
                }
            }
        });

        sortedChildren.addAll(children);

        return sortedChildren;
    }


    public Category saveCategory(Category category) {
        Category parent = category.getParent();
        if (parent != null) {
            String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIds += parent.getId() + "-";
            category.setAllParentIDs(allParentIds);
        }
        setCategoryLevel(category);
        return categoryRepo.saveAndFlush(category);

    }


    public void setCategoryLevel(Category category) {

        if (category.getParent() == null) {
            category.setLevel(0);
        } else {

            category.setLevel(category.getParent().getLevel() + 1);
        }


        categoryRepo.saveAndFlush(category);

    }

    public void deleteCategory(Integer id) throws CategoryNotFoundException {
        try {
            categoryRepo.deleteById(id);

        } catch (NoSuchElementException ex) {

            throw new CategoryNotFoundException("Could not find any Category with ID " + id);
        }
    }

    public void UpdateCategoryEnableStatus(Integer id, Boolean enable) {
        disableCategoryAndSubcategories(id, enable);

    }

    @Transactional
    public void disableCategoryAndSubcategories(Integer id, Boolean enable) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));


        categoryRepo.enableCategory(id, enable);

        disableChildrenRecursively(category,enable);
    }

    private void disableChildrenRecursively(Category parent , Boolean enable) {
        List<Category> children = categoryRepo.findByParent(parent);
        for (Category child : children) {

            categoryRepo.enableCategory(child.getId(), enable);


            disableChildrenRecursively(child,  enable);
        }
    }





}



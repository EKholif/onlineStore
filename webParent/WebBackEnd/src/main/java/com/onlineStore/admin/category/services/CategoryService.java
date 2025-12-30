package com.onlineStore.admin.category.services;

import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.category.CategoryRepository;
import com.onlineStoreCom.entity.category.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class CategoryService {

    public static final int USERS_PER_PAGE = 5;

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

        // boolean isCreatingNew = (id==null|| id==0L);

        Category categoryByName = categoryRepo.findByName(name);
        Category categoryByAlias = categoryRepo.findByAlias(alias);

        // if (isCreatingNew){

        if (categoryByName != null && categoryByName.getId() != id) {
            return "DuplicateName";
        }

        // }else {

        if (categoryByAlias != null && categoryByAlias.getId() != id) {
            return "DuplicateAlies";
        }
        // }

        // if (categoryByAlias != null && categoryByAlias.getId() !=id){
        // return "DuplicateName";}
        //
        // }
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

    private void printCategoryHierarchy(Category category, List<Category> categoriesUsedInForm, int depth,
                                        String sortDir) {

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

            /*
             * AG-CAT-002: Hierarchical View Strategy
             * We page by ROOT Categories (e.g. 5 roots per page).
             * However, to display the tree structure, we must return the roots PLUSE their
             * expanded sub-categories.
             * This results in a list size > page size (e.g. 5 roots + 10 children = 15
             * rows).
             * This is INTENTIONAL behavior to preserve UI hierarchy.
             * The PageInfo statistics should reflect the Roots (total pages of trees), not
             * the total rows.
             */
            return listHierarchicalCategories(searchResult, sortDir);

        } else {
            /*
             * AG-CAT-003: Row-Based Pagination Strategy
             * User Requirement: Strict 5 rows per page.
             * Previous strategy (Root-Based) caused confusing "view explosion" (5 roots ->
             * 30+ rows).
             * New Strategy:
             * 1. Fetch ALL hierarchies (Memory Intensive - documented risk).
             * 2. Flatten to list.
             * 3. Apply standard pagination slicing (SubList).
             */

            // 1. Get ALL root categories (unsorted to build hierarchy correctly first, or
            // sorted if needed)
            // Ideally we need findAll() but built hierarchically.
            // Reuse listUsedForForm() logic which builds the full sorted tree.
            // But we need to support the 'sortDir' from arguments if possible,
            // though listUsedForForm uses name/asc by default.

            // For MVP, we use the existing hierarchical builder with the requested sort.

            List<Category> fullTree = new ArrayList<>();
            // We need to fetch all roots to build a correct full tree for flattening
            // But findRootCategories(pageable) is limited.
            // We use findAll() and filter roots manually or use a repo method for all
            // roots.

            // Optimization: Fetch all and build tree.
            List<Category> allRoots = categoryRepo.findRootCategories(Pageable.unpaged()).getContent();
            fullTree = listHierarchicalCategories(allRoots, sortDir);

            // 2. Update PageInfo with Total ROWS
            pageInfo.setTotalElements(fullTree.size());
            pageInfo.setTotalPages((int) Math.ceil((double) fullTree.size() / USERS_PER_PAGE));

            // 3. Slice the list for current Page
            int start = (pageNum - 1) * USERS_PER_PAGE;
            int end = Math.min(start + USERS_PER_PAGE, fullTree.size());

            if (start > fullTree.size()) {
                return new ArrayList<>();
            }

            List<Category> pageContent = fullTree.subList(start, end);
            return pageContent;

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

        disableChildrenRecursively(category, enable);
    }

    private void disableChildrenRecursively(Category parent, Boolean enable) {
        List<Category> children = categoryRepo.findByParent(parent);
        for (Category child : children) {

            categoryRepo.enableCategory(child.getId(), enable);

            disableChildrenRecursively(child, enable);
        }
    }

}

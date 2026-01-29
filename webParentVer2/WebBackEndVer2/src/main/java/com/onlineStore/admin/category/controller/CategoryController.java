package com.onlineStore.admin.category.controller;

import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.category.controller.utility.CategoryCsvCategoryExporter;
import com.onlineStore.admin.category.controller.utility.CategoryExcelExporter;
import com.onlineStore.admin.category.controller.utility.CategoryPdfCategoryExporter;
import com.onlineStore.admin.category.services.CategoryService;
import com.onlineStore.admin.utility.paging.PagingAndSortingHelper;
import com.onlineStore.admin.utility.paging.PagingAndSortingParam;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@org.springframework.stereotype.Controller
public class CategoryController {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService service;

    @GetMapping("/categories/categories")
    public String listAllCategories() {
        LOGGER.info("Request received to list all categories (redirecting to page 1)");
        return "redirect:/categories/page/1?sortField=name&sortDir=asc";
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "categories", moduleURL = "/categories/page/") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {

        LOGGER.debug("Listing categories page: {} with sortField: {}, sortDir: {}", pageNum, helper.getSortField(),
                helper.getSortDir());

        Page<Category> page = service.listByPage(pageNum, helper.getSortField(), helper.getSortDir(),
                helper.getKeyword());
        helper.updateModelAttributes(pageNum, page);

        return "categories/categories";
    }

    @GetMapping("/categories/new-categories-form")
    public ModelAndView newCategoryForm() {

        ModelAndView model = new ModelAndView("categories/new-categories-form");

        Category category = new Category();
        category.setEnabled(true);

        List<Category> listCategory = service.listUsedForForm();

        model.addObject("id", 0L);
        model.addObject("label", "Parent Category :");
        model.addObject("category", category);
        model.addObject("listItems", listCategory);
        model.addObject("pageTitle", "Create new category");
        model.addObject("saveChanges", "/categories/save-category");

        return model;

    }

    // todo : rundom id

    @PostMapping("/categories/save-category")
    public ModelAndView saveNewCategory(@ModelAttribute Category category,
                                         RedirectAttributes redirectAttributes, @RequestParam("fileImage") MultipartFile multipartFile)
            throws IOException {

        Long tenantId = TenantContext.getTenantId();
        category.setTenantId(tenantId);

        // AG-REFACTOR: Delegate to Service
        service.saveCategory(category, multipartFile);

        redirectAttributes.addFlashAttribute("message", "The category has been saved successfully.");
        return new ModelAndView("redirect:/categories/categories");
    }

    @GetMapping("/categories/edit/{id}")
    public ModelAndView editCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        ModelAndView model = new ModelAndView("categories/new-categories-form");

        try {

            Category existCategory = service.findById(id);
            List<Category> listCategory = service.listUsedForForm();

            model.addObject("label", "Parent Category :");
            model.addObject("category", existCategory);
            model.addObject("id", id);
            model.addObject("listItems", listCategory);
            model.addObject("pageTitle", " Edit : category ID :  " + id);
            model.addObject("saveChanges", "/categories/save-edit-category");

            return model;

        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return new ModelAndView("redirect:/categories/categories");

        }
    }

    @PostMapping("/categories/save-edit-category")
    public ModelAndView saveUpdatedCategory(@RequestParam(name = "id") Integer id, @ModelAttribute Category category,
                                        RedirectAttributes redirectAttributes,
                                        @RequestParam("fileImage") MultipartFile multipartFile) throws CategoryNotFoundException, IOException {

        Category updateCategory = service.findById(id);

        // AG-REFACTOR: Copy props then delegate to Service
        // Note: Controller previously handled "if file empty -> copy excluding image".
        // Service now handles "if file empty -> keep existing logic".
        // But here we are binding form to 'category' object.
        // We need to merge carefully.

        if (multipartFile.isEmpty()) {
            BeanUtils.copyProperties(category, updateCategory, "id", "image", "tenantId");
            // Service.saveCategory(entity, emptyFile) will just save entity.
            service.saveCategory(updateCategory, multipartFile);
        } else {
            // File present
            BeanUtils.copyProperties(category, updateCategory, "id", "tenantId");
            // Service will set new image name from file.
            service.saveCategory(updateCategory, multipartFile);
        }

        redirectAttributes.addFlashAttribute("message", "The Category ID " + id + " has been updated successfully.");

        return new ModelAndView("redirect:/categories/categories");
    }

    @GetMapping("/delete-category/{id}")
    public ModelAndView deleteCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes)
            throws CategoryNotFoundException {

        try {
            // AG-REFACTOR: Service handles cleanup
            service.deleteCategory(id);
            redirectAttributes.addFlashAttribute("message", "The Category ID " + id + " has been deleted.");
        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", "Category Not Found or Error Deleting");
        }
        return new ModelAndView("redirect:/categories/categories");
    }

    @GetMapping("/category/{id}/enable/{status}")
    public ModelAndView UpdateUserStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enable,
                                         RedirectAttributes redirectAttributes) {

        String status = enable ? "enable" : " disable";

        service.UpdateCategoryEnableStatus(id, enable);
        String message = " the user Id :   " + id + " has bean  " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return new ModelAndView("redirect:/categories/categories");

    }

    @PostMapping("/categories/deleteCategories")
    public ModelAndView deleteCategory(
            @RequestParam(name = "selectedCategory", required = false) List<Integer> selectedCategory,
            RedirectAttributes redirectAttributes) {

        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            for (Integer id : selectedCategory) {
                try {
                    service.deleteCategory(id);
                } catch (Exception e) {
                    // ignore partial
                }
            }
            redirectAttributes.addFlashAttribute("message", "Selected Categories have been deleted.");
        }

        return new ModelAndView("redirect:/categories/categories");
    }

    @GetMapping("/categories/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<Category> listCategories = service.listUsedForForm();
        CategoryCsvCategoryExporter userCsvExporter = new CategoryCsvCategoryExporter();
        userCsvExporter.export(listCategories, response);

    }

    @GetMapping("/categories/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<Category> categoryList = service.listUsedForForm();

        CategoryExcelExporter categoryExcelExporter = new CategoryExcelExporter();
        categoryExcelExporter.export(categoryList, response);

    }

    @GetMapping("/categories/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<Category> categoryList = service.listUsedForForm();

        CategoryPdfCategoryExporter categoryPdfCategoryExporter = new CategoryPdfCategoryExporter();
        categoryPdfCategoryExporter.export(categoryList, response);

    }

}

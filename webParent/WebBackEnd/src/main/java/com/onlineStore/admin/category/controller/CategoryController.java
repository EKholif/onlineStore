package com.onlineStore.admin.category.controller;

import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.category.controller.utility.CategoryCsvCategoryExporter;
import com.onlineStore.admin.category.controller.utility.CategoryExcelExporter;
import com.onlineStore.admin.category.controller.utility.CategoryPdfCategoryExporter;
import com.onlineStore.admin.category.services.CategoryService;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStore.admin.utility.paging.PagingAndSortingHelper;
import com.onlineStore.admin.utility.paging.PagingAndSortingParam;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@org.springframework.stereotype.Controller
public class CategoryController {
    @Autowired
    private CategoryService service;

    @GetMapping("/categories/categories")
    public String listAllCategories() {
        return "redirect:/categories/page/1?sortField=name&sortDir=asc";
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "categories", moduleURL = "/categories/page/") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {

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
    public ModelAndView saveNewUCategory(@ModelAttribute Category category,
                                         RedirectAttributes redirectAttributes, @RequestParam("fileImage") MultipartFile multipartFile)
            throws IOException {
        redirectAttributes.addFlashAttribute("message", "the category   has been saved successfully.  ");

        Long tenantId = TenantContext.getTenantId();
        category.setTenantId(tenantId);
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

            category.setImage(fileName);
            Category savedCategory = service.saveCategory(category);

            String uploadDir = savedCategory.getImageDir();

            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {

            service.saveCategory(category);
        }

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
    public ModelAndView saveUpdaterUser(@RequestParam(name = "id") Integer id, @ModelAttribute Category category,
                                        RedirectAttributes redirectAttributes,
                                        @RequestParam("fileImage") MultipartFile multipartFile) throws CategoryNotFoundException, IOException {

        redirectAttributes.addFlashAttribute("message", "the Category Id : " + id + " has been updated successfully. ");

        Category updateCategory = service.findById(id);

        if (multipartFile.isEmpty()) {
            BeanUtils.copyProperties(category, updateCategory, "id", "image", "tenantId");
            service.saveCategory(updateCategory);

        } else if (!multipartFile.isEmpty()) {

            FileUploadUtil.cleanDir(updateCategory.getImageDir());
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            // AG-ASSET-PATH-012: Update path for category updates
            String uploadDir = updateCategory.getImageDir();
            category.setImage(fileName);
            BeanUtils.copyProperties(category, updateCategory, "id", "tenantId");

            service.saveCategory(updateCategory);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        }

        return new ModelAndView("redirect:/categories/categories");
    }

    @GetMapping("/delete-category/{id}")
    public ModelAndView deleteCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes)
            throws CategoryNotFoundException, IOException {

        try {
            if (service.existsById(id)) {
                FileUploadUtil.cleanDir(service.findById(id).getImageDir());

                service.deleteCategory(id);
                redirectAttributes.addFlashAttribute("message", "the Category ID: " + id + " has been Deleted");
            } else {
                redirectAttributes.addFlashAttribute("message", "the Category ID: " + id + " Category Not Found");

            }

            return new ModelAndView("redirect:/categories/categories");
        } catch (CategoryNotFoundException | IOException ex) {
            redirectAttributes.addFlashAttribute("message", " Category Not Found");
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
            RedirectAttributes redirectAttributes) throws CategoryNotFoundException, IOException {

        redirectAttributes.addFlashAttribute("message", "the Category ID: " + selectedCategory + " has been Deleted");
        ModelAndView model = new ModelAndView("categories/categories");

        model.addObject("label", selectedCategory);

        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            for (Integer id : selectedCategory) {
                FileUploadUtil.cleanDir(service.findById(id).getImageDir());
                service.deleteCategory(id);
            }
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

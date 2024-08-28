package com.onlineStore.admin.category.controller;


import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.category.controller.utility.CategoryCsvCategoryExporter;
import com.onlineStore.admin.category.controller.utility.CategoryExcelExporter;
import com.onlineStore.admin.category.controller.utility.CategoryPdfCategoryExporter;
import com.onlineStore.admin.category.services.PageInfo;
import com.onlineStore.admin.category.services.CategoryService;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStoreCom.entity.category.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController

public class CategoryController {
    @Autowired
    private CategoryService service;


    @GetMapping("/categories/categories")


    public ModelAndView listAllCategories() {
        ModelAndView model = new ModelAndView("categories/categories");

        return listByPage(1, "name", "asc", null);
    }


    @GetMapping("/categories/page/{pageNum}")
    public ModelAndView listByPage(@PathVariable(name = "pageNum") int pageNum,
                                   @Param("sortFiled") String sortFiled, @Param("sortDir") String sortDir,
                                   @Param("keyWord") String keyWord) {

        ModelAndView model = new ModelAndView("categories/categories");

        PageInfo pageInfo = new PageInfo();

        List<Category> categoryPagePage = service.listByPage(pageInfo, pageNum, sortFiled, sortDir, keyWord);

        PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper(model, "categories", sortFiled, sortDir, keyWord, pageNum, categoryPagePage);

        return pagingAndSortingHelper.listByPage(pageInfo, "categories");

    }


    @GetMapping("/categories/new-categories-form")
    public ModelAndView newCategoryForm() {

        ModelAndView model = new ModelAndView("categories/new-categories-form");

        Category category = new Category();
        category.setEnable(true);

        List<Category> listCategory = service.listUsedForForm();

        model.addObject("id", 0L);
        model.addObject("label", "Parent Category :");
        model.addObject("category", category);


        PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper("categories", listCategory); // Corrected listName
        return pagingAndSortingHelper.newForm(model, "category", category);

    }


//    todo : rundom id


    @PostMapping("/categories/save-category")
    public ModelAndView saveNewUCategory(@ModelAttribute Category category,
                                         RedirectAttributes redirectAttributes
            , @RequestParam("fileImage") MultipartFile multipartFile)
            throws IOException {
        redirectAttributes.addFlashAttribute("message", "the category   has been saved successfully.  ");


        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

            category.setImage(fileName);
            Category savedCategory = service.saveCategory(category);

            String dirName = "categories-photos/";
            String uploadDir = dirName + savedCategory.getId();

            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {

            service.saveCategory(category);
        }

        return new ModelAndView("redirect:/categories/categories");


    }

    @GetMapping("/categories/edit/{id}")
    public ModelAndView editCategory(@PathVariable(name = "id") long id, RedirectAttributes redirectAttributes) {
        ModelAndView model = new ModelAndView("categories/new-categories-form");


        try {

            Category existCategory = service.findById(id);
            List<Category> listCategory = service.listUsedForForm();

            model.addObject("label", "Parent Category :");

            PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper("categories", listCategory); // Corrected listName
            return pagingAndSortingHelper.editForm(model, "category", existCategory, id);


        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return new ModelAndView("redirect:/categories/categories");


        }
    }

    @PostMapping("/categories/save-edit-category/")
    public ModelAndView saveUpdaterUser(@RequestParam(name = "id") Long id, @ModelAttribute Category category, RedirectAttributes redirectAttributes,
                                        @RequestParam("fileImage") MultipartFile multipartFile) throws CategoryNotFoundException, IOException {

        redirectAttributes.addFlashAttribute("message", "the Category Id : " + id + " has been updated successfully. ");

        Category updateCategory = service.findById(id);


        if (multipartFile.isEmpty()) {
            BeanUtils.copyProperties(category, updateCategory, "id", "image");
            service.saveCategory(updateCategory);

        } else if (!multipartFile.isEmpty()) {

            FileUploadUtil.cleanDir(updateCategory.getImageDir());
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            String uploadDir = "categories-photos/" + updateCategory.getId();
            category.setImage(fileName);
            BeanUtils.copyProperties(category, updateCategory, "id");

            service.saveCategory(updateCategory);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        }

        return new ModelAndView("redirect:/categories/categories");
    }


    @GetMapping("/delete-category/{id}")
    public ModelAndView deleteCategory(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) throws CategoryNotFoundException, IOException {

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
    public ModelAndView UpdateUserStatus(@PathVariable("id") Long id, @PathVariable("status") boolean enable,
                                         RedirectAttributes redirectAttributes) {
        service.UpdateCategoryEnableStatus(id, enable);
        String status = enable ? "enable" : " disable";
        String message = " the user Id :   " + id + " has bean  " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return new ModelAndView("redirect:/categories/categories");

    }

    @PostMapping("/categories/deleteCategories")
    public ModelAndView deleteCategory(@RequestParam(name = "selectedCategory", required = false) List<Long> selectedCategory,
                                       RedirectAttributes redirectAttributes) throws CategoryNotFoundException, IOException {

        redirectAttributes.addFlashAttribute("message", "the Category ID: " + selectedCategory + " has been Deleted");
        ModelAndView model = new ModelAndView("categories/categories");

        model.addObject("label", selectedCategory);

        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            for (Long id : selectedCategory) {
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




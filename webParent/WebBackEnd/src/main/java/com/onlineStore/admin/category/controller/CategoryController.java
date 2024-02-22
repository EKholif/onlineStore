package com.onlineStore.admin.category.controller;


import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.category.CategoryRepository;
import com.onlineStore.admin.category.controller.utility.CategoryCsvCategoryExporter;
import com.onlineStore.admin.category.controller.utility.CategoryExcelExporter;
import com.onlineStore.admin.category.controller.utility.CategoryPdfCategoryExporter;
import com.onlineStore.admin.category.services.CategoryPageInfo;
import com.onlineStore.admin.category.services.CategoryService;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStoreCom.entity.prodact.Category;
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
    private CategoryService service ;
    @Autowired
    private CategoryRepository categoryRepository;


    @GetMapping("/categories/categories")
    public ModelAndView listAllCategories( ) {
        ModelAndView model = new ModelAndView("/categories/categories");
        model.addObject("pageTitle","List categories" );

        model.addObject("categories", service.listAll());
        return listByPage(1,"name","asc",null);
    }


    @GetMapping("/categories/page/{pageNum}")
    public ModelAndView listByPage(@PathVariable(name = "pageNum") int pageNum,
                                   @Param("sortFiled") String sortFiled, @Param("sortDir") String sortDir,
                                   @Param("keyWord") String keyWord)
    {

        ModelAndView model = new ModelAndView("/categories/categories");

        CategoryPageInfo pageInfo = new CategoryPageInfo();

        List<Category> categoryPagePage = service.listByPage(pageInfo,pageNum, sortFiled, sortDir, keyWord);

//        List<Category>categoryList= categoryPagePage.getContent();



//        List<Category>categoryList=  service.listByPage(pageInfo,pageNum, sortFiled, sortDir, keyWord);

        long startCount = (pageNum - 1) * CategoryService.USERS_PER_PAGE + 1;
        long endCount = startCount + CategoryService.USERS_PER_PAGE - 1;
        if (endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }

        String reverseSortDir = sortDir.equals("asc") ? "dsc" : "asc";

        model.addObject("totalItems", pageInfo.getTotalElements());
        model.addObject("totalPages",  pageInfo.getTotalPages());
        model.addObject("currentPage", pageNum);
        model.addObject("sortFiled" , sortFiled);
        model.addObject("sortDir" , sortDir);

        model.addObject("endCount", endCount);
        model.addObject("startCont", startCount);
        model.addObject("categories", categoryPagePage);
        model.addObject("reverseSortDir", reverseSortDir);
        model.addObject("keyWord", keyWord);

        return  model;
    }







    @GetMapping("/categories/new-category-form")
    public ModelAndView newCategoryForm() {

        ModelAndView model = new ModelAndView("/categories/new-category-form");

        Category newCategory = new Category();

        newCategory.setEnable(true);

        List<Category> listCategory = service.listUsedForForm();


        model.addObject("category", newCategory);
        model.addObject("label", "Parent Category :");

        model.addObject("listCategory",listCategory );
        model.addObject("pageTitle","Creat new Category" );
        model.addObject("saveChanges", "/categories/save-category");
        model.addObject("id", 0L);
        return model;

    }


//    todo : rundom id


    @PostMapping("/categories/save-category")
    public ModelAndView saveNewUCategory(@ModelAttribute  Category category,
                                         RedirectAttributes redirectAttributes
            , @RequestParam("fileImage") MultipartFile multipartFile)
            throws IOException {
        redirectAttributes.addFlashAttribute("message", "the category   has been saved successfully.  ");


        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

            System.out.println(fileName);

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

    @GetMapping("/category/edit/{id}")
    public ModelAndView editCategory(@PathVariable (name="id")long id, RedirectAttributes redirectAttributes) {


        try {
            ModelAndView model = new ModelAndView("/categories/new-category-form");

            Category ExistCategory = service.getCategory(id);

            List<Category> listCategory = service.listUsedForForm();

            model.addObject("category", ExistCategory);

            model.addObject("label", "Parent Category :");

            model.addObject("listCategory", listCategory);
            model.addObject("saveChanges", "/categories/save-edit-category/");

            model.addObject("pageTitle", "Edit " + ExistCategory.getName() + " (ID: " + id + ")");
            model.addObject("id", id);


            return model;

        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return new ModelAndView("redirect:/categories/categories");


        }
    }

    @PostMapping( "/categories/save-edit-category/")
    public ModelAndView saveUpdaterUser(@RequestParam ( name="id") Long id,@ModelAttribute  Category category, RedirectAttributes redirectAttributes,
                                       @RequestParam("fileImage") MultipartFile multipartFile ) throws CategoryNotFoundException, IOException {

        redirectAttributes.addFlashAttribute("message", "the Category Id : " + id+  " has been updated successfully. ");

        Category updateCategory =service.getCategory(id);



            if (multipartFile.isEmpty()) {
                BeanUtils.copyProperties(  category,updateCategory,"id",  "image");
                service.saveCategory(updateCategory);

               } else if (!multipartFile.isEmpty()) {

                FileUploadUtil.cleanDir(updateCategory.getImageDir());
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                String uploadDir = "categories-photos/" + updateCategory.getId();
                category.setImage(fileName);
                BeanUtils.copyProperties( category,updateCategory,"id"  );

                service.saveCategory(updateCategory);
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

            }

        return new ModelAndView("redirect:/categories/categories");
    }



    @GetMapping ("/categories/delete-Category/{id}")
        public ModelAndView deleteCategory(@PathVariable (name = "id")Long id, RedirectAttributes redirectAttributes) throws CategoryNotFoundException, IOException {

        try {
            if (categoryRepository.existsById(id)) {
                FileUploadUtil.cleanDir( service.getCategory(id).getImageDir());

           service.deleteCategory(id);
                redirectAttributes.addFlashAttribute("message", "the Category ID: " + id + " has been Deleted");
            } else {
                redirectAttributes.addFlashAttribute("message", "the Category ID: " + id + " Category Not Found");

            }

            return new ModelAndView("redirect:/categories/categories");
        }

        catch (CategoryNotFoundException | IOException ex) {
            redirectAttributes.addFlashAttribute("message", " Category Not Found");
        }
            return new ModelAndView("redirect:/categories/categories");
        }


    @GetMapping("/category/{id}/enable/{status}")
    public ModelAndView UpdateUserStatus (@PathVariable("id")Long id, @PathVariable("status") boolean enable,
                                    RedirectAttributes redirectAttributes){
        service.UpdateCategoryEnableStatus(id,enable);
        String status = enable ? "enable" :" disable";
        String message = " the user Id :   " + id +" has bean  " +status ;
        redirectAttributes.addFlashAttribute("message", message);

        return new ModelAndView("redirect:/categories/categories");

       }

    @PostMapping("/categories/deleteCategories")
    public ModelAndView deleteCategory(@RequestParam(name = "selectedCategory", required = false) List<Long> selectedCategory,
                              RedirectAttributes redirectAttributes) throws CategoryNotFoundException, IOException {

        redirectAttributes.addFlashAttribute("message", "the Category ID: " + selectedCategory + " has been Deleted");
        ModelAndView model = new ModelAndView("/categories/categories");

        model.addObject("label", selectedCategory);

        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            for (Long userId : selectedCategory) {
                FileUploadUtil.cleanDir( service.getCategory(userId).getImageDir());
                service.deleteCategory(userId);
            }
        }
        return new ModelAndView("redirect:/categories/categories");
    }


@GetMapping("/categories/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<Category> listCategories = service.listAll();
        CategoryCsvCategoryExporter userCsvExporter = new CategoryCsvCategoryExporter();
        userCsvExporter.export(listCategories,response);

}
    @GetMapping("/categories/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<Category> categoryList = service.listAll();

        CategoryExcelExporter categoryExcelExporter = new  CategoryExcelExporter();
        categoryExcelExporter.export(categoryList,response);


    }
    @GetMapping("/categories/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<Category> categoryList = service.listAll();

        CategoryPdfCategoryExporter categoryPdfCategoryExporter = new  CategoryPdfCategoryExporter();
        categoryPdfCategoryExporter.export(categoryList,response);

    }


    }




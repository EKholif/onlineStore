package com.onlineStore.admin.brand;


import com.onlineStore.admin.brand.utility.BrandCsvExporter;
import com.onlineStore.admin.brand.utility.BrandExcelExporter;
import com.onlineStore.admin.brand.utility.BrandPdfExporter;
import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.category.services.CategoryService;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStore.admin.category.controller.PagingAndSortingHelper;
import com.onlineStore.admin.category.services.PageInfo;
import com.onlineStoreCom.entity.brand.Brand;
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
public class BrandController {
    @Autowired
    private BrandService service;
    @Autowired
    private CategoryService categoryService;
    public static Integer USERS_PER_PAGE = 4;


    @GetMapping("/brands/brands")
    public ModelAndView listAllUsers() {
        ModelAndView model = new ModelAndView("brands/brands");


        return listByPage(1, "name", "dsc", null);
    }


    @GetMapping("/brands/page/{pageNum}")
    public ModelAndView listByPage(@PathVariable(name = "pageNum") int pageNum,
                                   @Param("sortFiled") String sortFiled, @Param("sortDir") String sortDir,
                                   @Param("keyWord") String keyWord) {

        ModelAndView model = new ModelAndView("brands/brands");
        PageInfo pageInfo = new PageInfo();

        List<Brand> listByPage = service.listByPage(pageInfo, pageNum, sortFiled, sortDir, keyWord);


        PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper
                (model, "brands", sortFiled, sortDir, keyWord, pageNum, listByPage);

        pagingAndSortingHelper.listByPage(pageInfo, "brands");
        return model;
    }

    @GetMapping("/brands/new-brands-form")
    public ModelAndView newBrandForm() {
        ModelAndView model = new ModelAndView("brands/new-brands-form");


        List<Category> listCategory = categoryService.listUsedForForm();
        Brand brand = new Brand();

        model.addObject("label", " Category :");
        model.addObject("id", 0L);

        PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper("brands", listCategory); // Corrected listName
        return pagingAndSortingHelper.newForm(model, "brand", brand);


    }


    @PostMapping("/brands/save-brand")
    public ModelAndView saveNewUCategory(@ModelAttribute Brand brand,
                                         RedirectAttributes redirectAttributes
            , @RequestParam("fileImage") MultipartFile multipartFile)
            throws IOException {
        redirectAttributes.addFlashAttribute("message", "the brand has been saved successfully.  ");


        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));


            brand.setLogo(fileName);
            Brand savedBrand = service.saveBrand(brand);

            String dirName = "brands-photos/";
            String uploadDir = dirName + savedBrand.getId();

            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {

            service.saveBrand(brand);
        }

        return new ModelAndView("redirect:/brands/brands");

    }


    @GetMapping("/brand/edit/{id}")
    public ModelAndView editCategory(@PathVariable(name = "id") long id, RedirectAttributes redirectAttributes) {


        try {
            ModelAndView model = new ModelAndView("/brands/new-brands-form");

            Brand existCategory = service.findById(id);

            List<Category> listCategory = categoryService.listUsedForForm();


            PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper("brands", listCategory); // Corrected listName
            return pagingAndSortingHelper.editForm(model, "brand", existCategory, id);


        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return new ModelAndView("redirect:/brands/brands");
        }

    }

    @PostMapping("/brands/save-edit-brand/")
    public ModelAndView saveUpdaterUser(@RequestParam(name = "id") Long id, @ModelAttribute Brand brand, RedirectAttributes redirectAttributes,
                                        @RequestParam("fileImage") MultipartFile multipartFile) throws CategoryNotFoundException, IOException {

        redirectAttributes.addFlashAttribute("message", "the Category Id : " + id + " has been updated successfully. ");

        Brand updateBrand = service.findById(id);

        if (multipartFile.isEmpty()) {
            BeanUtils.copyProperties(brand, updateBrand, "id", "logo");
            service.saveBrand(updateBrand);

        } else if (!multipartFile.isEmpty()) {

            FileUploadUtil.cleanDir(updateBrand.getImageDir());
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            String uploadDir = "brands-photos/" + updateBrand.getId();
            brand.setLogo(fileName);
            BeanUtils.copyProperties(brand, updateBrand, "id");

            service.saveBrand(updateBrand);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        }

        return new ModelAndView("redirect:/brands/brands");
    }


    @GetMapping("/brands/delete-brand/{id}")
    public ModelAndView deleteCategory(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {

        try {
            if (service.existsById(id)) {
                FileUploadUtil.cleanDir(service.findById(id).getImageDir());

                service.delete(id);
                redirectAttributes.addFlashAttribute("message", "the Brand ID: " + id + " has been Deleted");
            } else {
                redirectAttributes.addFlashAttribute("message", "the Brand ID: " + id + " Brand Not Found");

            }

            return new ModelAndView("redirect:/brands/brands");
        } catch (BrandNotFoundException | IOException | CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", " Brand Not Found");
        }
        return new ModelAndView("redirect:/brands/brands");
    }


    @PostMapping("/delete-brands")
    public ModelAndView deleteBrand(@RequestParam(name = "selectedForDelete", required = false) List<Long> selectedForDelete,
                                    RedirectAttributes redirectAttributes) throws IOException, BrandNotFoundException, CategoryNotFoundException {

        ModelAndView model = new ModelAndView("/brands/brands");
        redirectAttributes.addFlashAttribute("message", "the Brand ID: " + selectedForDelete + " has been Deleted");

        System.out.println(selectedForDelete);

        model.addObject("label", selectedForDelete);

        if (selectedForDelete != null && !selectedForDelete.isEmpty()) {
            for (Long id : selectedForDelete) {
                FileUploadUtil.cleanDir(service.findById(id).getImageDir());
                service.delete(id);
            }
        }
        return new ModelAndView("redirect:/brands/brands");
    }
//

    @GetMapping("/brands/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<Brand> listbrands = service.listAll();
        BrandCsvExporter userCsvExporter = new BrandCsvExporter();
        userCsvExporter.export(listbrands, response);
    }

    @GetMapping("/brands/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {

        List<Brand> listbrands = service.listAll();
        BrandExcelExporter BrandExcelExporter = new BrandExcelExporter();
        BrandExcelExporter.export(listbrands, response);

    }

    @GetMapping("/brands/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<Brand> listbrands = service.listAll();

        BrandPdfExporter brandPdfExporter = new BrandPdfExporter();
        brandPdfExporter.export(listbrands, response);

    }

}





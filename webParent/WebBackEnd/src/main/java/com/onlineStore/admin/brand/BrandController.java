package com.onlineStore.admin.brand;

import com.onlineStore.admin.brand.utility.BrandCsvExporter;
import com.onlineStore.admin.brand.utility.BrandExcelExporter;
import com.onlineStore.admin.brand.utility.BrandPdfExporter;
import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.category.services.CategoryService;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStore.admin.utility.paging.PagingAndSortingHelper;
import com.onlineStore.admin.utility.paging.PagingAndSortingParam;
import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.tenant.TenantContext;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class BrandController {

    @Autowired
    private BrandService service;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/brands/brands")
    public String listAllBrands() {
        return "redirect:/brands/page/1?sortField=name&sortDir=asc";
    }

    @GetMapping("/brands/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "brands", moduleURL = "/brands/page/") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {

        Page<Brand> page = service.listByPage(pageNum, helper.getSortField(), helper.getSortDir(), helper.getKeyword());
        helper.updateModelAttributes(pageNum, page);

        return "brands/brands";
    }

    @GetMapping("/brands/new-brands-form")
    public ModelAndView newBrandForm() {
        ModelAndView model = new ModelAndView("brands/new-brands-form");

        List<Category> listCategory = categoryService.listUsedForForm();
        Brand brand = new Brand();

        model.addObject("label", " Category :");
        model.addObject("id", 0L);
        model.addObject("brand", brand);
        model.addObject("listItems", listCategory);
        model.addObject("pageTitle", "Create new brand");
        model.addObject("saveChanges", "/brands/save-brand");

        return model;
    }

    @PostMapping("/brands/save-brand")
    public ModelAndView saveNewUCategory(@ModelAttribute Brand brand, RedirectAttributes redirectAttributes,
                                         @RequestParam("fileImage") MultipartFile multipartFile) throws IOException {
        redirectAttributes.addFlashAttribute("message", "the brand has been saved successfully.  ");

        Long tenantId = TenantContext.getTenantId();
        brand.setTenantId(tenantId);

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

            brand.setLogo(fileName);
            Brand savedBrand = service.saveBrand(brand);

            // AG-ASSET-PATH-001: Use new hierarchical asset structure
            String uploadDir = "tenants/" + tenantId + "/assets/brands/" + savedBrand.getId();

            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {

            service.saveBrand(brand);
        }

        return new ModelAndView("redirect:/brands/brands");

    }

    @GetMapping("/brand/edit/{id}")
    public ModelAndView editCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {

        try {
            ModelAndView model = new ModelAndView("/brands/new-brands-form");

            Brand existBrand = service.findById(id);

            List<Category> listCategory = categoryService.listUsedForForm();

            model.addObject("brand", existBrand);
            model.addObject("id", id);
            model.addObject("listItems", listCategory);
            model.addObject("pageTitle", " Edit : brand ID :  " + id);
            model.addObject("saveChanges", "/brands/save-edit-brand");
            model.addObject("label", " Category :"); // Kept from original context if needed, though logic might differ
            // slightly.

            return model;

        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return new ModelAndView("redirect:/brands/brands");
        }

    }

    @PostMapping("/brands/save-edit-brand")
    public ModelAndView saveUpdaterUser(@RequestParam(name = "id") Integer id, @ModelAttribute Brand brand,
                                        RedirectAttributes redirectAttributes, @RequestParam("fileImage") MultipartFile multipartFile)
            throws CategoryNotFoundException, IOException {

        redirectAttributes.addFlashAttribute("message", "the Category Id : " + id + " has been updated successfully. ");

        Brand updateBrand = service.findById(id);

        if (multipartFile.isEmpty()) {
            BeanUtils.copyProperties(brand, updateBrand, "id", "logo");
            service.saveBrand(updateBrand);

        } else if (!multipartFile.isEmpty()) {

            FileUploadUtil.cleanDir(updateBrand.getImageDir());
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            // AG-ASSET-PATH-002: Use new hierarchical asset structure for updates
            String uploadDir = "tenants/" + updateBrand.getTenantId() + "/assets/brands/" + updateBrand.getId();
            brand.setLogo(fileName);
            BeanUtils.copyProperties(brand, updateBrand, "id");

            service.saveBrand(updateBrand);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        }

        return new ModelAndView("redirect:/brands/brands");
    }

    @GetMapping("/brands/delete-brand/{id}")
    public ModelAndView deleteCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {

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
    public ModelAndView deleteBrand(
            @RequestParam(name = "selectedForDelete", required = false) List<Integer> selectedForDelete,
            RedirectAttributes redirectAttributes)
            throws IOException, BrandNotFoundException, CategoryNotFoundException {

        ModelAndView model = new ModelAndView("/brands/brands");
        redirectAttributes.addFlashAttribute("message", "the Brand ID: " + selectedForDelete + " has been Deleted");

        model.addObject("label", selectedForDelete);

        if (selectedForDelete != null && !selectedForDelete.isEmpty()) {
            for (Integer id : selectedForDelete) {
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

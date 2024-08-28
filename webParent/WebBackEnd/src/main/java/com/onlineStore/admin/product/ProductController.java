package com.onlineStore.admin.product;


import com.onlineStore.admin.brand.BrandNotFoundException;
import com.onlineStore.admin.brand.BrandService;
import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.category.controller.PagingAndSortingHelper;
import com.onlineStore.admin.category.services.CategoryService;
import com.onlineStore.admin.category.services.PageInfo;
import com.onlineStore.admin.product.service.ProductService;
import com.onlineStore.admin.utility.FileUploadUtil;
import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.product.Product;
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
public class ProductController {

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    public static Integer USERS_PER_PAGE = 4;


    @GetMapping("/products/products")
    public ModelAndView listAllUsers() {
        ModelAndView model = new ModelAndView("products/products");

        List<Product> listItems = productService.listAll();
        model.addObject("products", listItems);
        model.addObject("pageTitle", "products");

        return listByPage(1, "name", "dsc", null);
    }


    @GetMapping("/products/page/{pageNum}")
    public ModelAndView listByPage(@PathVariable(name = "pageNum") int pageNum,
                                   @Param("sortFiled") String sortFiled, @Param("sortDir") String sortDir,
                                   @Param("keyWord") String keyWord) {


        ModelAndView model = new ModelAndView("products/products");
        PageInfo pageInfo = new PageInfo();

        List<Product> listByPage = productService.listByPage(pageInfo, pageNum, sortFiled, sortDir, keyWord);

        PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper
                (model, "products", sortFiled, sortDir, keyWord, pageNum, listByPage);

        pagingAndSortingHelper.listByPage(pageInfo, "products");
        return model;
    }

    @GetMapping("/products/new-products-form")
    public ModelAndView newBrandForm() {
        ModelAndView model = new ModelAndView("products/new-products-form");

        List<Brand> listBrands = brandService.listAll();
        List<Category> listCategory = categoryService.listUsedForForm();

        Product product = new Product();
        Integer numberOfExistingExtraImage = product.getImages().size();

        model.addObject("numberOfExistingExtraImage", numberOfExistingExtraImage);
        model.addObject("listBrands", listBrands);
        model.addObject("label-brand", " Brand Name :");
        model.addObject("label", "Main Image");

        model.addObject("label-category", " Category :");
        model.addObject("listCategory", listCategory);

        PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper("products", listCategory); // Corrected listName
        return pagingAndSortingHelper.newForm(model, "product", product);

    }


    @PostMapping("/products/save-product")
    public ModelAndView saveNewUCategory(RedirectAttributes redirectAttributes,
                                         @ModelAttribute Product product
            , @RequestParam(name = "fileImage") MultipartFile mainImageMultipartFile
            , @RequestParam(name = "extraImage") MultipartFile[] extraImageMultipart
            , @RequestParam(name = "detailIDs", required = false) String[] detailIDs
            , @RequestParam(name = "detailNames", required = false) String[] detailNames
            , @RequestParam(name = "detailValues", required = false) String[] detailValues) throws IOException {

        redirectAttributes.addFlashAttribute("message", "the brand has been saved successfully.  ");

        setMainImageName(mainImageMultipartFile, product);
        setExtraImageNames(extraImageMultipart, product);
        setProductDetails(detailIDs, detailNames, detailValues, product);

        Product saveProduct = productService.saveProduct(product);


        saveUpLoadImages(mainImageMultipartFile, extraImageMultipart, saveProduct);
        return new ModelAndView("redirect:/products/products");
    }


    static void setProductDetails(String[] detailIDs, String[] detailNames,
                                  String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;

        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];

            Integer id = count;

            if (id != 0) {

                product.addProductDetails(name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addProductDetails(name, value);

            }
        }
    }


    private void saveUpLoadImages(MultipartFile mainImageMultipartFile,
                                  MultipartFile[] extraImageMultipart, Product saveProduct) throws IOException {

        if (!mainImageMultipartFile.isEmpty()) {

            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipartFile.getOriginalFilename()));
            String dirName = "Products-photos/";
            String uploadDir = dirName + saveProduct.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipartFile);
        }


        if (extraImageMultipart.length > 0) {

            String dirName = "Products-photos/";
            String uploadDir = dirName + saveProduct.getId() + "/extras/";

            for (MultipartFile extramultipartFile : extraImageMultipart) {

                if (!extramultipartFile.isEmpty()) {

                    String extraImageFileName = StringUtils.cleanPath(Objects.requireNonNull(extramultipartFile.getOriginalFilename()));
                    FileUploadUtil.saveFile(uploadDir, extraImageFileName, extramultipartFile);
                }
            }
        }
    }

    private void setExtraImageNames(MultipartFile[] extraImageMultipart, Product product) {

        if (extraImageMultipart.length > 0) {

            for (MultipartFile multipartFile : extraImageMultipart) {

                if (!multipartFile.isEmpty()) {
                    String extraImageFileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                    product.addExtraImages(extraImageFileName);
                }
            }
        }
    }

    private void setMainImageName(MultipartFile mainImageMultipartFile, Product product) {

        if (!mainImageMultipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipartFile.getOriginalFilename()));
            product.setMainImage(fileName);
        }
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public ModelAndView UpdateUserStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enable,
                                         RedirectAttributes redirectAttributes) {
        productService.UpdateProductEnableStatus(id, enable);
        String status = enable ? "enable" : " disable";
        String message = " the user Id :   " + id + " has bean  " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return new ModelAndView("redirect:/products/products");

    }


    @GetMapping("/products/edit/{id}")
    public ModelAndView editCategory(@PathVariable("id") Integer id, RedirectAttributes ra) {

        ModelAndView model = new ModelAndView("products/new-products-form");


        List<Brand> listBrands = brandService.listAll();
        List<Category> listCategory = categoryService.listUsedForForm();


        Product product = productService.findById(id);

        Integer numberOfExistingExtraImage = product.getImages().size();
        model.addObject("numberOfExistingExtraImage", numberOfExistingExtraImage);

        model.addObject("listImage", listBrands);
        model.addObject("label-image", " Image :");

        model.addObject("listDetails", listBrands);
        model.addObject("label-details", " Details :");

        model.addObject("listBrands", listBrands);
        model.addObject("label-brand", " Brand Name :");


        model.addObject("label-category", " Category :");
        model.addObject("listCategory", listCategory);


        PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper("products", listCategory); // Corrected listName
        return pagingAndSortingHelper.editForm(model, "product", product, id);

    }



    @GetMapping("/products/detail/{id}")
    public ModelAndView detailProductView(@PathVariable("id") Integer id, RedirectAttributes ra) {

        ModelAndView model = new ModelAndView("products/product_detail_modal");


        Product product = productService.findById(id);

        model.addObject("product", product);

        Integer numberOfExistingExtraImage = product.getImages().size();
        model.addObject("numberOfExistingExtraImage", numberOfExistingExtraImage);

        model.addObject("label-image", " Image :");

        model.addObject("label-details", " Details :");

        model.addObject("label-brand", " Brand Name :");

        model.addObject("label-category", " Category :");

        return  model;

    }





    @PostMapping("/products/save-edit-product")
    public ModelAndView saveUpdaterUser(@RequestParam(name = "id") int id, RedirectAttributes redirectAttributes,
                                        @ModelAttribute Product product,
                                        @RequestParam("fileImage") MultipartFile mainImageMultipartFile,
                                        @RequestParam("extraImage") MultipartFile[] extraImageMultipart,
                                        @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                                        @RequestParam(name = "detailNames", required = false) String[] detailNames,
                                        @RequestParam(name = "detailValues", required = false) String[] detailValues
    ) throws CategoryNotFoundException, IOException {

        redirectAttributes.addFlashAttribute("message", "the Category Id : " + id + " has been updated successfully. ");

        Product updateProduct = productService.findById(id);

        setProductDetails(detailIDs, detailNames, detailValues, updateProduct);

        setMainImageName(mainImageMultipartFile, product);
        setExtraImageNames(extraImageMultipart, product);

        if (mainImageMultipartFile.isEmpty()) {

            product.setMainImage(updateProduct.getMainImage());


        }

        BeanUtils.copyProperties(product, updateProduct, "id", "name", "alias");

        BeanUtils.copyProperties(product, updateProduct, "id");

        Product saveProduct = productService.saveProduct(updateProduct);

        saveUpLoadImages(mainImageMultipartFile, extraImageMultipart, saveProduct);


        return new ModelAndView("redirect:/products/products");
    }


    @GetMapping("/products/delete-product/{id}")
    public ModelAndView deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {


        redirectAttributes.addFlashAttribute("message", deleteFilesAndFolder(id));

        return new ModelAndView("redirect:/products/products");
    }


    @PostMapping("/delete-Products")
    public ModelAndView deleteBrand(@RequestParam(name = "selectedForDelete", required = false) List<Integer> selectedForDelete,
                                    RedirectAttributes redirectAttributes) throws IOException, ProductNotFoundException, CategoryNotFoundException, BrandNotFoundException {

        ModelAndView model = new ModelAndView("/products/products");
        redirectAttributes.addFlashAttribute("message", "the Product ID: " + selectedForDelete + " has been Deleted");


        model.addObject("label", selectedForDelete);

        if (selectedForDelete != null && !selectedForDelete.isEmpty()) {
            for (Integer id : selectedForDelete) {
                redirectAttributes.addFlashAttribute("message", deleteFilesAndFolder(id));
            }
        }
        return new ModelAndView("redirect:/products/products");
    }


    private String deleteFilesAndFolder(Integer id) {
        try {
            if (productService.existsById(id)) {

                FileUploadUtil.deleteDir(productService.findById(id).getExtraImageDir());
                FileUploadUtil.deleteDir(productService.findById(id).getImageDir());
                productService.deleteProduct(id);
                return "the Product ID: " + id + " has been Deleted";
            } else {
                return "the Product ID: " + id + " Product Not Found";
            }
        } catch (IOException | CategoryNotFoundException e) {
            return " Brand Not Found";
        }

    }


//    @GetMapping("/products/export/csv")
//        public void exportToCsv(HttpServletResponse response) throws IOException {
//            List<Brand> listProducts = service.listAll();
//            BrandCsvExporter userCsvExporter = new BrandCsvExporter();
//            userCsvExporter.export(listProducts,response);
//    }
//
//    @GetMapping("/products/export/excel")
//    public void exportToExcel(HttpServletResponse response) throws IOException {
//
//        List<Brand> listProducts = service.listAll();
//        BrandExcelExporter BrandExcelExporter = new  BrandExcelExporter();
//        BrandExcelExporter.export(listProducts,response);
//
//    }
//    @GetMapping("/products/export/pdf")
//    public void exportToPdf(HttpServletResponse response) throws IOException {
//        List<Brand> listProducts = service.listAll();
//
//        BrandPdfExporter brandPdfExporter = new  BrandPdfExporter();
//        brandPdfExporter.export( listProducts,response);
//
//    }

}





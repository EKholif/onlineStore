package com.onlineStore.admin.product;

import com.onlineStore.admin.brand.BrandService;
import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStore.admin.category.services.CategoryService;
import com.onlineStore.admin.product.service.ProductService;
import com.onlineStore.admin.utility.paging.PagingAndSortingHelper;
import com.onlineStore.admin.utility.paging.PagingAndSortingParam;
import com.onlineStoreCom.entity.brand.Brand;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.tenant.TenantContext;
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
public class ProductController {

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/products/products")
    public String listAllProducts() {
        return "redirect:/products/page/1?sortField=name&sortDir=asc";
    }

    /**
     * Root: Manage Platform Products
     */
    @GetMapping("/products/manage")
    public String managePlatformProducts() {
        // Enforce Root? Service layer handles it, but semantic URL checks help.
        // For now, redirect to standard list.
        return "redirect:/products/page/1?sortField=name&sortDir=asc";
    }

    /**
     * Tenant: Manage My Products
     */
    @GetMapping("/my-products")
    public String manageMyProducts() {
        return "redirect:/products/page/1?sortField=name&sortDir=asc";
    }
    @Autowired
    private com.onlineStoreCom.analytics.ProductAnalyticsService analyticsService;

    @GetMapping("/products/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "products", moduleURL = "/products/page/") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum) {

        // AG-OBSERVABILITY: Replaced System.out with SLF4J
        LOGGER.debug("Received request for Product Page: {}", pageNum);

        // AG-ANALYTICS: Log View Asynchronously
        // We track "List View" as a generic view maybe? Or only Detail View?
        // Typically "View" means detail page. But let's log listing access too if
        // useful.
        // For strictness, let's look at `detailProductView` for the actual analytics
        // event.
        // But the user rules said "Log View".
        // Let's check detailProductView method.

        Page<Product> page = productService.listByPage(pageNum, helper.getSortField(), helper.getSortDir(),
                helper.getKeyword());
        helper.updateModelAttributes(pageNum, page);

        return "products/products";
    }

    @GetMapping("/products/new-products-form")
    public ModelAndView newProductForm(
            @RequestParam(name = "type", required = false) com.onlineStoreCom.entity.product.ProductType type) {
        ModelAndView model = new ModelAndView("products/new-products-form");

        List<Brand> listBrands = brandService.listAll();
        List<Category> listCategory = categoryService.listUsedForForm();

        Product product = new Product();
        if (type != null) {
            product.setProductType(type);
            // AG-UNIFIED-003: Pre-configure flags based on type
            if (type == com.onlineStoreCom.entity.product.ProductType.SERVICE ||
                    type == com.onlineStoreCom.entity.product.ProductType.BOOKING) {
                product.setHasShipping(false);
                product.setHasScheduling(true);
            } else {
                product.setHasShipping(true);
                product.setHasScheduling(false);
            }
        }

        Integer numberOfExistingExtraImage = product.getImages().size();

        model.addObject("numberOfExistingExtraImage", numberOfExistingExtraImage);
        model.addObject("listBrands", listBrands);
        model.addObject("label-brand", " Brand Name :");
        model.addObject("label", "Main Image");

        model.addObject("label-category", " Category :");
        model.addObject("listCategory", listCategory);

        model.addObject("product", product);
        model.addObject("listItems", listCategory);
        model.addObject("pageTitle", "Create new product");
        model.addObject("saveChanges", "/products/save-product");

        return model;
    }

    @PostMapping("/products/save-product")
    public ModelAndView saveProduct(RedirectAttributes redirectAttributes,
            @ModelAttribute Product product, @RequestParam(name = "fileImage") MultipartFile mainImageMultipartFile,
            @RequestParam(name = "extraImage") MultipartFile[] extraImageMultipart,
            @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
            @RequestParam(name = "detailNames", required = false) String[] detailNames,
            @RequestParam(name = "detailValues", required = false) String[] detailValues) throws IOException {

        redirectAttributes.addFlashAttribute("message", "the product has been saved successfully.");

        Long tenantId = TenantContext.getTenantId();
        product.setTenantId(tenantId);

        // AG-REFACTOR: Validating Detail Arrays before processing
        productService.processProductDetails(detailIDs, detailNames, detailValues, product, tenantId);

        // AG-REFACTOR: Delegate logic and storage to Service
        productService.saveProduct(product, mainImageMultipartFile, extraImageMultipart);

        return new ModelAndView("redirect:/products/products");
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
    public ModelAndView editProduct(@PathVariable("id") Integer id, RedirectAttributes ra) {

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
        // model.addObject("listCategory", listCategory);

        model.addObject("product", product);
        model.addObject("id", id);
        model.addObject("listItems", listCategory);
        model.addObject("pageTitle", " Edit : product ID :  " + id);
        model.addObject("saveChanges", "/products/save-edit-product");

        return model;

    }

    @GetMapping("/products/detail/{id}")
    public ModelAndView detailProductView(@PathVariable("id") Integer id, RedirectAttributes ra) {

        // AG-ANALYTICS: Track specific product view
        if (id != null) {
            analyticsService.logView(id);
        }

        ModelAndView model = new ModelAndView("products/product_detail_modal");

        Product product = productService.findById(id);

        model.addObject("product", product);

        Integer numberOfExistingExtraImage = product.getImages().size();
        model.addObject("numberOfExistingExtraImage", numberOfExistingExtraImage);

        model.addObject("label-image", " Image :");

        model.addObject("label-details", " Details :");

        model.addObject("label-brand", " Brand Name :");

        model.addObject("label-category", " Category :");

        return model;

    }

    @PostMapping("/products/save-edit-product")
    public ModelAndView saveUpdaterUser(@RequestParam(name = "id") int id, RedirectAttributes redirectAttributes,
            @ModelAttribute Product product,
            @RequestParam("fileImage") MultipartFile mainImageMultipartFile,
            @RequestParam("extraImage") MultipartFile[] extraImageMultipart,
            @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
            @RequestParam(name = "detailNames", required = false) String[] detailNames,
            @RequestParam(name = "detailValues", required = false) String[] detailValues)
            throws CategoryNotFoundException, IOException {

        redirectAttributes.addFlashAttribute("message", "the Product Id : " + id + " has been updated successfully. ");

        Product updateProduct = productService.findById(id);
        Long tenantId = TenantContext.getTenantId();

        // AG-MARKETPLACE-003: Protect Global Products from Tenant modification
        if (updateProduct.getTenantId() != null && updateProduct.getTenantId() == 0L
                && (tenantId != null && tenantId != 0L)) {
            redirectAttributes.addFlashAttribute("message",
                    "Error: You cannot edit Global Products directly. Please duplicate it to your catalog.");
            return new ModelAndView("redirect:/products/products");
        }

        // Merge Details
        productService.processProductDetails(detailIDs, detailNames, detailValues, updateProduct, tenantId);

        // Copy Properties from Form
        BeanUtils.copyProperties(product, updateProduct, "id", "name", "alias", "tenantId", "mainImage", "images",
                "details");

        // Delegate Saving (Service handles Image Logic and Saving)
        // Note: We pass the updateProduct which now has modified properties, and the
        // images.
        // Service check: if mainImage is empty, it does NOT overwrite
        // product.mainImage.
        // BUT product.mainImage is now updateProduct.mainImage from the DB (because we
        // ignored it in copyProperties? No, we didn't).
        // Wait, copyProperties syntax is (source, target, ignoreProperties).
        // I ignored "mainImage", "images", "details" so they preserve DB state.
        // This allows Service to perform logic: if new image uploaded, set it. If not,
        // keep existing.
        // Perfect.

        productService.saveProduct(updateProduct, mainImageMultipartFile, extraImageMultipart);

        return new ModelAndView("redirect:/products/products");
    }

    @GetMapping("/products/delete-product/{id}")
    public ModelAndView deleteProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "the Product ID: " + id + " has been Deleted");
        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", "Product Not Found");
        }
        return new ModelAndView("redirect:/products/products");
    }

    @PostMapping("/delete-Products")
    public ModelAndView deleteProducts(
            @RequestParam(name = "selectedForDelete", required = false) List<Integer> selectedForDelete,
            RedirectAttributes redirectAttributes) {

        ModelAndView model = new ModelAndView("/products/products");
        redirectAttributes.addFlashAttribute("message", "the Product ID: " + selectedForDelete + " has been Deleted");

        model.addObject("label", selectedForDelete);

        if (selectedForDelete != null && !selectedForDelete.isEmpty()) {
            for (Integer id : selectedForDelete) {
                try {
                    productService.deleteProduct(id);
                } catch (CategoryNotFoundException e) {
                    // ignore partial failures
                }
            }
        }
        return new ModelAndView("redirect:/products/products");
    }


    // @GetMapping("/products/export/csv")
    // public void exportToCsv(HttpServletResponse response) throws IOException {
    // List<Brand> listProducts = service.listAll();
    // BrandCsvExporter userCsvExporter = new BrandCsvExporter();
    // userCsvExporter.export(listProducts,response);
    // }
    //
    // @GetMapping("/products/export/excel")
    // public void exportToExcel(HttpServletResponse response) throws IOException {
    //
    // List<Brand> listProducts = service.listAll();
    // BrandExcelExporter BrandExcelExporter = new BrandExcelExporter();
    // BrandExcelExporter.export(listProducts,response);
    //
    // }
    // @GetMapping("/products/export/pdf")
    // public void exportToPdf(HttpServletResponse response) throws IOException {
    // List<Brand> listProducts = service.listAll();
    //
    // BrandPdfExporter brandPdfExporter = new BrandPdfExporter();
    // brandPdfExporter.export( listProducts,response);
    //
    // }

}

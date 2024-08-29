package com.example.frontend.product;

import com.example.frontend.category.CategoryService;
import com.onlineStore.admin.category.CategoryNotFoundException;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ProductController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;


    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,
                                        Model model) {
        try {

            Category category = categoryService.getCategory(alias);

            List<Category> listCategoryParents = categoryService.getCategoryParents(category);
            model.addAttribute("pageTitle", category.getName());
            model.addAttribute("listCategoryParents", listCategoryParents);

//    List<Category> listCategories = categoryService.listNoChildrenCategories();
            Set<Category> listCategoryChildren = category.getChildren();
            model.addAttribute("listCategories", listCategoryChildren);

            Set<Product> listProduct = productService.setAll(category.getId());
            model.addAttribute("listProduct", listProduct);

// Initialize a set to store all products from the child categories
            Set<Product> listCatProduct = new HashSet<>();

// Iterate over each child category
            for (Category childCategory : listCategoryChildren) {
                // Fetch products for each child category using productService
                Set<Product> childCategoryProducts = productService.setAll(childCategory.getId());

                // Add the products to the main product set
                listCatProduct.addAll(childCategoryProducts);
            }
            model.addAttribute("listCatProduct", listCatProduct);

// Now, listProduct contains all products associated with the child categories





            return "product/products_by_category";
        } catch (CategoryNotFoundException ex) {
            return "error/404";
        }
    }


    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias,
                                     @PathVariable("pageNum") int pageNum,
                                     Model model) {
        try {
            Category category = categoryService.getCategory(alias);
            List<Category> listCategoryParents = categoryService.getCategoryParents(category);
//            Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());

            model.addAttribute("currentPage", pageNum);
            model.addAttribute("pageTitle", category.getName());
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("category", category);

            return "product/products_by_category";
        } catch (CategoryNotFoundException ex) {
            return "error/404";
        }
    }

//    @GetMapping("/p/{product_alias}")
//    public String viewProductDetail(@PathVariable("product_alias") String alias, Model model,
//                                    HttpServletRequest request) {
//
//        try {
//            Product product = productService.getProduct(alias);
//            List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
//            Page<Review> listReviews = reviewService.list3MostVotedReviewsByProduct(product);
//
//            Customer customer = controllerHelper.getAuthenticatedCustomer(request);
//
//            if (customer != null) {
//                boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
//                voteService.markReviewsVotedForProductByCustomer(listReviews.getContent(), product.getId(), customer.getId());
//
//                if (customerReviewed) {
//                    model.addAttribute("customerReviewed", customerReviewed);
//                } else {
//                    boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
//                    model.addAttribute("customerCanReview", customerCanReview);
//                }
//            }
//
//            model.addAttribute("listCategoryParents", listCategoryParents);
//            model.addAttribute("product", product);
//            model.addAttribute("listReviews", listReviews);
//            model.addAttribute("pageTitle", product.getShortName());
//
//            return "product/product_detail";
//        } catch (ProductNotFoundException e) {
//            return "error/404";
//        }
//    }
//
//    @GetMapping("/search")
//    public String searchFirstPage(String keyword, Model model) {
//        return searchByPage(keyword, 1, model);
//    }
//
//    @GetMapping("/search/page/{pageNum}")
//    public String searchByPage(String keyword,
//                               @PathVariable("pageNum") int pageNum,
//                               Model model) {
//        Page<Product> pageProducts = productService.search(keyword, pageNum);
//        List<Product> listResult = pageProducts.getContent();
//
//        long startCount = (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
//        long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
//        if (endCount > pageProducts.getTotalElements()) {
//            endCount = pageProducts.getTotalElements();
//        }
//
//        model.addAttribute("currentPage", pageNum);
//        model.addAttribute("totalPages", pageProducts.getTotalPages());
//        model.addAttribute("startCount", startCount);
//        model.addAttribute("endCount", endCount);
//        model.addAttribute("totalItems", pageProducts.getTotalElements());
//        model.addAttribute("pageTitle", keyword + " - Search Result");
//
//        model.addAttribute("keyword", keyword);
//        model.addAttribute("searchKeyword", keyword);
//        model.addAttribute("listResult", listResult);
//
//        return "product/search_result";
//    }
}

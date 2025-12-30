package frontEnd.product;

import com.onlineStoreCom.entity.Review.Review;
import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.product.Product;
import frontEnd.category.CategoryService;
import frontEnd.review.ReviewService;
import frontEnd.review.vote.ReviewVoteService;
import frontEnd.utilites.ControllerHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Set;

@Controller
public class ProductController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReviewVoteService voteService;
    @Autowired
    private ControllerHelper controllerHelper;

    // @GetMapping("/p/{category_alias}")
    // public String viewCategoryFirstPage(@PathVariable("category_alias") String
    // alias,
    // Model model) {
    // try {
    // Category category = categoryService.getCategory(alias);
    //
    // List<Category> listCategoryParents =
    // categoryService.getCategoryParents(category);
    //
    // Set<Category> listCategoryChildren =
    // categoryService.listCategoryChildren(category);
    //
    // Set<Product> listProduct = productService.setAll(category.getId());
    // Set<Product> listCatProduct =
    // productService.childCategoryProduct(listCategoryChildren);
    //
    // model.addObject("listProduct", listProduct);
    // model.addObject("pageTitle", category.getName());
    // model.addObject("listCategoryParents", listCategoryParents);
    // model.addObject("listCategories", listCategoryChildren);
    // model.addObject("listCatProduct", listCatProduct);
    //
    // return "product/products_by_category";
    // } catch (CategoryNotFoundException ex) {
    // return "error/404";
    // }
    // }

    @GetMapping("/c/{category_alias}")
    public ModelAndView listAllUsers(@PathVariable("category_alias") String alias) throws CategoryNotFoundException {
        String category = categoryService.getCategory(alias).getName();
        String sortDir = "asc";
        String sortField = "name";
        return listByPage(alias, 1, sortField, sortDir, alias);
    }

    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public ModelAndView listByPage(@PathVariable("category_alias") String alias,
                                   @PathVariable("pageNum") int pageNum,
                                   @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                                   @RequestParam(name = "keyWord", required = false) String keyWord)
            throws CategoryNotFoundException {

        ModelAndView model = new ModelAndView("product/products_by_category");

        Category category = categoryService.getCategory(alias);
        List<Category> listCategoryParents = categoryService.getCategoryParents(category);
        Set<Category> listCategoryChildren = categoryService.listCategoryChildren(category);

        model.addObject("listCategoryParents", listCategoryParents);
        model.addObject("listCategories", listCategoryChildren);

        PageInfo pageInfo = new PageInfo();
        List<Product> listProduct = productService.listByCategory(pageInfo, pageNum, sortField, sortDir, keyWord,
                alias);

        PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper(
                model, "products", sortField, sortDir, keyWord, pageNum, listProduct);
        pagingAndSortingHelper.listByPage(pageInfo, "products");

        return model;
    }

    @GetMapping("/d/listProductOnSale/page/{pageNum}")
    public ModelAndView listProductOnSale(@PathVariable("pageNum") int pageNum,
                                          @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                                          @RequestParam(name = "keyWord", required = false) String keyWord) {

        String pageTitle = "OnlineStore";

        ModelAndView model = new ModelAndView("index");
        PageInfo pageInfo = new PageInfo();
        List<Category> listCategories = categoryService.listNoParentCategories();
        List<Product> listProductOnSale = productService.allItemOnSale(pageInfo, pageNum, sortField, sortDir, keyWord),
                k;

        long startCount = (long) 1;
        long endCount = startCount + CategoryService.USERS_PER_PAGE - 1;
        if (endCount > pageInfo.getTotalElements()) {
            endCount = pageInfo.getTotalElements();
        }

        Sort reverseSortDir = Sort.by(sortField);
        reverseSortDir = sortDir.equals("asc") ? reverseSortDir.ascending() : reverseSortDir.descending();

        String modelUrl = " /d/" + "listProductOnSale" + "/page/";

        model.addObject("sortDir", sortDir);
        model.addObject("sortField", sortField);
        model.addObject("endCount", endCount);
        model.addObject("currentPage", pageNum);
        model.addObject("startCont", startCount);
        model.addObject("reverseSortDir", reverseSortDir);
        model.addObject("totalPages", pageInfo.getTotalPages());
        model.addObject("totalItems", pageInfo.getTotalElements());
        model.addObject("listCategories", listCategories);
        model.addObject("listCategoryParents", listCategories);
        model.addObject("listProductOnSale", listProductOnSale);
        model.addObject("pageTitle", pageTitle);
        model.addObject("modelUrl", modelUrl);
        return model;
    }

    @Autowired
    private frontEnd.question.QuestionService questionService;

    @GetMapping("/p/{product_alias}")
    public ModelAndView viewProductDetail(@PathVariable("product_alias") String alias, HttpServletRequest request)
            throws ProductNotFoundException, CategoryNotFoundException {

        Product product = productService.findByAlis(alias);
        ModelAndView model = new ModelAndView("product/product_detail");

        Category category = product.getCategory();
        List<Category> listCategoryParents = categoryService.getCategoryParents(category);
        Page<Review> listReviews = reviewService.list3MostVotedReviewsByProduct(product);

        // AG-QA-IMP-001: Fetch Q&A Data
        // Load Top 3 Questions
        List<com.onlineStoreCom.entity.question.Question> listQuestions = questionService
                .getTop3Questions(product.getId());
        int numberOfQuestions = questionService.getNumberOfQuestions(product.getId());
        int numberOfAnsweredQuestions = questionService.getNumberOfAnsweredQuestions(product.getId());

        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        if (customer != null) {
            boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
            voteService.markReviewsVotedForProductByCustomer(listReviews.getContent(), product.getId(),
                    customer.getId());

            // Map Vote Status for Questions
            questionService.markQuestionsVotedForProductByCustomer(listQuestions, product.getId(), customer.getId());

            if (customerReviewed) {
                model.addObject("customerReviewed", customerReviewed);
            } else {
                boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
                model.addObject("customerCanReview", customerCanReview);
            }
        }

        model.addObject("listCategoryParents", listCategoryParents);
        model.addObject("listReviews", listReviews);

        // AG-QA-IMP-001: Add Q&A attributes
        model.addObject("listQuestions", listQuestions);
        model.addObject("numberOfQuestions", numberOfQuestions); // Used in View All link
        model.addObject("numberOfAnsweredQuestions", numberOfAnsweredQuestions); // Used in Header stats

        model.addObject("pageTitle", product.getName());
        model.addObject("product", product);

        return model;
    }

    @GetMapping("/products/page/{pageNum}")
    public ModelAndView listByPage(@PathVariable(name = "pageNum") int pageNum,
                                   @Param("sortField") String sortField, @Param("sortDir") String sortDir,
                                   @Param("keyWord") String keyWord) {

        ModelAndView model = new ModelAndView("product/products_by_category");
        PageInfo pageInfo = new PageInfo();

        List<Product> listByPage = productService.listByCategory(pageInfo, pageNum, sortField, sortDir, keyWord,
                keyWord);

        PagingAndSortingHelper pagingAndSortingHelper = new PagingAndSortingHelper(model, "products", sortField,
                sortDir, keyWord, pageNum, listByPage);

        pagingAndSortingHelper.listByPage(pageInfo, "products");
        return model;
    }

    // @GetMapping("/p/{product_alias}")
    // public String viewProductDetail(@PathVariable("product_alias") String alias,
    // Model model,
    // HttpServletRequest request) {
    //
    // try {
    // Product product = productService.getProduct(alias);
    // List<Category> listCategoryParents =
    // categoryService.getCategoryParents(product.getCategory());
    // Page<Review> listReviews =
    // reviewService.list3MostVotedReviewsByProduct(product);
    //
    // Customer customer = controllerHelper.getAuthenticatedCustomer(request);
    //
    // if (customer != null) {
    // boolean customerReviewed = reviewService.didCustomerReviewProduct(customer,
    // product.getId());
    // voteService.markReviewsVotedForProductByCustomer(listReviews.getContent(),
    // product.getId(), customer.getId());
    //
    // if (customerReviewed) {
    // model.addObject("customerReviewed", customerReviewed);
    // } else {
    // boolean customerCanReview = reviewService.canCustomerReviewProduct(customer,
    // product.getId());
    // model.addObject("customerCanReview", customerCanReview);
    // }
    // }
    //
    // model.addObject("listCategoryParents", listCategoryParents);
    // model.addObject("product", product);
    // model.addObject("listReviews", listReviews);
    // model.addObject("pageTitle", product.getShortName());
    //
    // return "product/product_detail";
    // } catch (ProductNotFoundException e) {
    // return "error/404";
    // }
    // }
    //
    // @GetMapping("/search")
    // public String searchFirstPage(String keyword, Model model) {
    // return searchByPage(keyword, 1, model);
    // }
    //
    // @GetMapping("/search/page/{pageNum}")
    // public String searchByPage(String keyword,
    // @PathVariable("pageNum") int pageNum,
    // Model model) {
    // Page<Product> pageProducts = productService.search(keyword, pageNum);
    // List<Product> listResult = pageProducts.getContent();
    //
    // Integer startCount = (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE +
    // 1;
    // Integer endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
    // if (endCount > pageProducts.getTotalElements()) {
    // endCount = pageProducts.getTotalElements();
    // }
    //
    // model.addObject("currentPage", pageNum);
    // model.addObject("totalPages", pageProducts.getTotalPages());
    // model.addObject("startCount", startCount);
    // model.addObject("endCount", endCount);
    // model.addObject("totalItems", pageProducts.getTotalElements());
    // model.addObject("pageTitle", keyword + " - Search Result");
    //
    // model.addObject("keyword", keyword);
    // model.addObject("searchKeyword", keyword);
    // model.addObject("listResult", listResult);
    //
    // return "product/search_result";
    // }
}

package frontEnd;

import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.product.Product;
import frontEnd.category.CategoryService;
import frontEnd.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        List<Category> listCategories = categoryService.listNoParentCategories();
        List<Product> listProductOnSale = productService.getOnSaleProducts();

        System.out.println("DEBUG: Categories found: " + listCategories.size());
        System.out.println("DEBUG: On Sale Products found: " + listProductOnSale.size());

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("listProductOnSale", listProductOnSale);

        // Add attributes required by index.html fragments (sort/search/pagination)
        model.addAttribute("modelUrl", "/");
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        model.addAttribute("totalElements", listProductOnSale.size());
        model.addAttribute("sortField", "name");
        model.addAttribute("sortDir", "asc");
        model.addAttribute("keyWord", null);

        return "index";
    }

    @GetMapping("/debug/products")
    @org.springframework.web.bind.annotation.ResponseBody
    public List<Product> debugProducts() {
        // Return ALL products to inspect their 'enable' and 'discountPercent' values
        return productService.listAll();
    }

    @GetMapping("/login")
    public String viewLoginPage(
            @org.springframework.web.bind.annotation.RequestParam(name = "redirect", required = false) String redirect,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            if (redirect != null) {
                model.addAttribute("redirect", redirect);
            }
            return "/login";
        }

        return "redirect:/";
    }

    @GetMapping("/about")
    public String aboutPage() {

        return "/about/about";
    }

}

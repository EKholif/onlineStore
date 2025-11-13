package frontEnd;

import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.product.Product;
import frontEnd.category.CategoryService;
import frontEnd.product.CategoryNotFoundException;
import frontEnd.product.PageInfo;
import frontEnd.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class MainController {


    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;






    @GetMapping("/login")
    public String viewLoginPage() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "/login";
        }

        return "redirect:/";
    }

    @GetMapping("/about")
    public String aboutPage() {


        return "/about/about";
    }








}

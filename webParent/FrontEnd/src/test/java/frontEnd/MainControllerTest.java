package frontEnd;

import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.product.Product;
import frontEnd.category.CategoryService;
import frontEnd.product.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest(MainController.class)
// @AutoConfigureMockMvc is implied by @WebMvcTest
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductService productService;

    @MockBean
    private frontEnd.customer.CustomerRepository customerRepository;

    @MockBean
    private frontEnd.security.oauth.CustomerOAuth2UserService oAuth2UserService;

    @MockBean
    private frontEnd.security.oauth.OAuth2LoginSuccessHandler oauth2LoginHandler;

    @MockBean
    private frontEnd.setting.service.SettingService settingService;

    @Test
    public void testViewHomePage() throws Exception {
        // Arrange
        Category cat1 = new Category();
        cat1.setId(1);
        cat1.setName("Electronics");

        Product prod1 = new Product();
        prod1.setId(1);
        prod1.setName("Laptop");

        when(categoryService.listNoParentCategories()).thenReturn(List.of(cat1));
        when(productService.getOnSaleProducts()).thenReturn(List.of(prod1));
        when(settingService.getGenlSettings()).thenReturn(java.util.Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("listCategories", "listProductOnSale"))
                .andExpect(model().attribute("listCategories", hasSize(1)))
                .andExpect(model().attribute("listProductOnSale", hasSize(1)))
                .andExpect(model().attribute("currentPage", 1));
    }

    @Test
    @WithAnonymousUser
    public void testViewLoginPage_Anonymous() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("/login"));
        // Note: Controller returns "/login" (with slash)
    }

    // TODO: Add test for authenticated user redirection if Spring Security context
    // is easily mockable here
    // For now, Anonymous test covers the basic logic branch
}

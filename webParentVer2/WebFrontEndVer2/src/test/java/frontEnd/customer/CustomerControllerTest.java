package frontEnd.customer;

import com.onlineStoreCom.entity.customer.Customer;
import frontEnd.security.CustomerUserDetails;
import frontEnd.security.oauth.CustomerAuthenticationHelper;
import frontEnd.setting.service.SettingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AG-FRONT-CUST-001: Testing Customer Registration & Account Management.
 * Critical Business Path: Ensures new users can register and view forms.
 */
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private SettingService settingService;

    @MockBean
    private CustomerAuthenticationHelper authHelper;

    // Security & Global Mocks
    @MockBean
    private frontEnd.security.oauth.CustomerOAuth2UserService oAuth2UserService;
    @MockBean
    private frontEnd.security.oauth.OAuth2LoginSuccessHandler oauth2LoginHandler;
    @MockBean
    private frontEnd.customer.CustomerRepository customerRepository;

    @MockBean
    private jakarta.persistence.EntityManagerFactory entityManagerFactory;

    @Test
    public void testShowRegistrationForm() throws Exception {
        Customer customer = new Customer();
        customer.setEnabled(true);
        // Ensure image is set if needed by template, though CustomerUserDetails handles
        // getImagePath

        CustomerUserDetails userDetails = new CustomerUserDetails(customer);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/registration"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("register/register_form"))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attributeExists("pageTitle"));
    }
}

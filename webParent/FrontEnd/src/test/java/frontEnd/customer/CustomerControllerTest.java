package frontEnd.customer;

import frontEnd.security.oauth.CustomerAuthenticationHelper;
import frontEnd.setting.service.SettingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("register/register_form"))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attributeExists("pageTitle"));
    }
}

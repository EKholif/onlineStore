package frontEnd.customerAddress;

import com.onlineStoreCom.entity.customer.Customer;
import frontEnd.customer.CustomerService;
import frontEnd.utilites.ControllerHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AG-FRONT-ADDR-001: Testing Address Management.
 * Business Path: Customers must be able to manage shipping addresses.
 */
@WebMvcTest(AddressController.class)
public class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private ControllerHelper controllerHelper;

    // Global Security Mocks
    @MockBean
    private frontEnd.security.oauth.CustomerOAuth2UserService oAuth2UserService;
    @MockBean
    private frontEnd.security.oauth.OAuth2LoginSuccessHandler oauth2LoginHandler;
    @MockBean
    private frontEnd.customer.CustomerRepository customerRepository;
    @MockBean
    private frontEnd.setting.service.SettingService settingService;

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "user", password = "password", roles = "CUSTOMER")
    public void testShowAddressBook() throws Exception {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setTenantId(1L);

        when(controllerHelper.getAuthenticatedCustomer(any())).thenReturn(customer);
        when(addressService.listAddressBook(customer)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/address_book"))
                .andExpect(status().isOk())
                .andExpect(view().name("address_book/addresses"))
                .andExpect(model().attributeExists("listAddresses"));
    }
}

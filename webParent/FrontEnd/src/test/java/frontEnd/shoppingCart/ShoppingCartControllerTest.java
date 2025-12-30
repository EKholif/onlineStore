package frontEnd.shoppingCart;

import com.onlineStoreCom.entity.customer.Customer;
import frontEnd.customer.CustomerService;
import frontEnd.customerAddress.AddressService;
import frontEnd.security.oauth.CustomerAuthenticationHelper;
import frontEnd.shipping.ShippingRateService;
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
 * AG-FRONT-CART-001: Testing Shopping Cart for reliable checkout.
 * Critical Business Path: Ensures customers can view their cart and shipping
 * calculation triggers correctly.
 */
@WebMvcTest(ShoppingCartController.class)
public class ShoppingCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private ShoppingCartService cartService;

    @MockBean
    private CustomerAuthenticationHelper customerAuthHelper;

    @MockBean
    private AddressService addressService;

    @MockBean
    private ShippingRateService shipService;

    // Mocks for Global Global Dependencies if any (e.g., SettingService)
    @MockBean
    private frontEnd.setting.service.SettingService settingService;
    @MockBean
    private frontEnd.security.oauth.CustomerOAuth2UserService oAuth2UserService;
    @MockBean
    private frontEnd.security.oauth.OAuth2LoginSuccessHandler oauth2LoginHandler;
    @MockBean
    private frontEnd.customer.CustomerRepository customerRepository; // Needed by some filters likely

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "john@example.com", password = "password", roles = "CUSTOMER")
    public void testViewCart_AuthenticatedCustomer() throws Exception {
        // Arrange
        Customer customer = new Customer();
        customer.setId(1);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");

        when(customerAuthHelper.getEmailOfAuthenticatedCustomer(any())).thenReturn("john@example.com");
        when(customerService.getCustomerByEmail("john@example.com")).thenReturn(customer);
        when(cartService.listCartItems(customer)).thenReturn(Collections.emptyList());
        when(addressService.getDefaultAddress(customer)).thenReturn(null);
        when(shipService.getShippingRateForCustomer(customer)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart/shopping_cart"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("shippingSupported"));
    }
}

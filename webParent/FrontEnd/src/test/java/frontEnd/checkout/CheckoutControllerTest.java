package frontEnd.checkout;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import com.onlineStoreCom.entity.shoppingCart.CartItem;
import frontEnd.customerAddress.AddressService;
import frontEnd.setting.service.SettingService;
import frontEnd.setting.settingBag.PaymentSettingBag;
import frontEnd.shipping.ShippingRateService;
import frontEnd.shoppingCart.ShoppingCartService;
import frontEnd.utilites.ControllerHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AG-FRONT-CHECKOUT-001: Testing Checkout Process.
 * Business Path: Final step of purchase execution.
 */
@WebMvcTest(CheckoutController.class)
public class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CheckoutService checkoutService;
    @MockBean
    private ControllerHelper controllerHelper;
    @MockBean
    private AddressService addressService;
    @MockBean
    private ShippingRateService shipService;
    @MockBean
    private ShoppingCartService cartService;
    @MockBean
    private frontEnd.order.OrderService orderService;
    @MockBean
    private SettingService settingService;
    @MockBean
    private frontEnd.checkout.paypal.PayPalService paypalService;

    // Security Mocks
    @MockBean
    private frontEnd.security.oauth.CustomerOAuth2UserService oAuth2UserService;
    @MockBean
    private frontEnd.security.oauth.OAuth2LoginSuccessHandler oauth2LoginHandler;
    @MockBean
    private frontEnd.customer.CustomerRepository customerRepository;

    @Test
    @org.springframework.security.test.context.support.WithMockUser(username = "user", password = "password", roles = "CUSTOMER")
    public void testShowCheckoutPage_Success() throws Exception {
        Customer customer = new Customer();
        customer.setId(1);

        ShippingRate rate = new ShippingRate();
        rate.setRate(10.0f);

        when(controllerHelper.getAuthenticatedCustomer(any())).thenReturn(customer);
        when(addressService.getDefaultAddress(customer)).thenReturn(null); // Use customer default
        when(shipService.getShippingRateForCustomer(customer)).thenReturn(rate);

        List<CartItem> cartItems = Collections.emptyList();
        when(cartService.listCartItems(customer)).thenReturn(cartItems);

        CheckoutInfo checkoutInfo = new CheckoutInfo();
        when(checkoutService.prepareCheckout(cartItems, rate)).thenReturn(checkoutInfo);

        when(settingService.getCurrencyCode()).thenReturn("USD");
        when(settingService.getPaymentSettings()).thenReturn(new PaymentSettingBag(Collections.emptyList()));

        mockMvc.perform(get("/checkout"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/checkout"))
                .andExpect(model().attributeExists("checkoutInfo"))
                .andExpect(model().attributeExists("paypalClientId"));
    }
}

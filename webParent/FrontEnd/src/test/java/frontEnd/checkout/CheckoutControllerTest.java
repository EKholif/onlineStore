package frontEnd.checkout;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.setting.Setting;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import com.onlineStoreCom.entity.shoppingCart.CartItem;
import frontEnd.customerAddress.AddressService;
import frontEnd.security.CustomerUserDetails;
import frontEnd.setting.service.SettingService;
import frontEnd.setting.settingBag.CurrencySettingBag;
import frontEnd.setting.settingBag.PaymentSettingBag;
import frontEnd.shipping.ShippingRateService;
import frontEnd.shoppingCart.ShoppingCartService;
import frontEnd.utilites.ControllerHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @MockBean
    private jakarta.persistence.EntityManagerFactory entityManagerFactory;

    @Test
    public void testShowCheckoutPage_Success() throws Exception {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setFirstName("User");
        customer.setLastName("Name");
        customer.setEmail("user");
        customer.setPassword("password");
        customer.setEnabled(true);

        CustomerUserDetails userDetails = new CustomerUserDetails(customer);

        com.onlineStoreCom.entity.setting.state.Country.Country country = new com.onlineStoreCom.entity.setting.state.Country.Country(
                1, "United States", "US");
        customer.setCountry(country);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

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

        Setting symbol = new Setting("CURRENCY_SYMBOL", "$", null);
        Setting position = new Setting("CURRENCY_SYMBOL_POSITION", "before", null);
        Setting decimalPoint = new Setting("DECIMAL_POINT_TYPE", "POINT", null);
        Setting thousandsPoint = new Setting("THOUSANDS_POINT_TYPE", "COMMA", null);
        Setting digits = new Setting("DECIMAL_DIGITS", "2", null);

        CurrencySettingBag currencySettings = new CurrencySettingBag(
                List.of(symbol, position, decimalPoint, thousandsPoint, digits));
        when(settingService.getCurrencySettings()).thenReturn(currencySettings);

        Setting bagPayment = new Setting("PAYPAL_API_CLIENT_ID", "test_client_id", null);
        when(settingService.getPaymentSettings()).thenReturn(new PaymentSettingBag(List.of(bagPayment)));

        // Mock General Settings for SettingFilter
        List<Setting> generalSettings = List.of(
                new Setting("CURRENCY_SYMBOLE", "$", null),
                new Setting("CURRENCY_SYMBOLE_POSITION", "before", null),
                new Setting("DECIMAL_POINT_TYPE", "POINT", null),
                new Setting("THOUSANDS_POINT_TYPE", "COMMA", null),
                new Setting("DECIMAL_DIGITS", "2", null));
        when(settingService.getGenlSettings()).thenReturn(generalSettings);

        mockMvc.perform(get("/checkout")
                        .requestAttr("CURRENCY_SYMBOLE", "$")
                        .requestAttr("CURRENCY_SYMBOLE_POSITION", "before")
                        .requestAttr("DECIMAL_POINT_TYPE", "POINT")
                        .requestAttr("THOUSANDS_POINT_TYPE", "COMMA")
                        .requestAttr("DECIMAL_DIGITS", 2))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout/checkout"))
                .andExpect(model().attributeExists("checkoutInfo"))
                .andExpect(model().attributeExists("paypalClientId"));
    }
}

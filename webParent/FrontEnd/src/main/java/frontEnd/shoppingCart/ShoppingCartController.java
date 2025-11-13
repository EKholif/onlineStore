package frontEnd.shoppingCart;
import com.onlineStoreCom.entity.address.Address;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.exception.CustomerNotFoundException;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import com.onlineStoreCom.entity.shoppingCart.CartItem;
import frontEnd.customer.CustomerService;
import frontEnd.customerAddress.AddressService;
import frontEnd.security.oauth.CustomerAuthenticationHelper;
import frontEnd.shipping.ShippingRateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ShoppingCartController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private ShoppingCartService cartService;
    @Autowired
    private CustomerAuthenticationHelper customerAuthHelper;
    @Autowired private AddressService addressService;
    @Autowired private ShippingRateService shipService;
    @GetMapping("/cart")
    public String viewCart(Model model, HttpServletRequest request) throws CustomerNotFoundException {

        Customer customer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = cartService.listCartItems(customer);
        float estimatedTotal = 0.0F;
        for (CartItem item : cartItems) {
            estimatedTotal += item.getSubtotal();
        }

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;
        boolean usePrimaryAddressAsDefault = false;

        if (defaultAddress != null) {
            shippingRate = shipService.getShippingRateForAddress(defaultAddress);
        } else {
            usePrimaryAddressAsDefault = true;
            shippingRate = shipService.getShippingRateForCustomer(customer);
        }

        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("shippingSupported", shippingRate != null);


        model.addAttribute("cartItems", cartItems);
        model.addAttribute("pageTitle", customer.getFullName());
        return "cart/shopping_cart";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = customerAuthHelper.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("No authenticated customer");
        }
        return customerService.getCustomerByEmail(email);
    }





}

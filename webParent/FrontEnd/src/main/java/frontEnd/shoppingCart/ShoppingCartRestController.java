package frontEnd.shoppingCart;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.exception.CustomerNotFoundException;
import com.onlineStoreCom.entity.shoppingCart.CartItem;
import frontEnd.customer.CustomerService;
import frontEnd.security.oauth.CustomerAuthenticationHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class ShoppingCartRestController {
    @Autowired
    private ShoppingCartService cartService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerAuthenticationHelper customerAuthHelper;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable("productId") Integer productId, @PathVariable("quantity") Integer quantity, HttpServletRequest request) {

        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = cartService.addProduct(productId, quantity, customer);

            return updatedQuantity + " item(s) of this product were added to your shopping cart.";
        } catch (CustomerNotFoundException ex) {
            return "You must login to add this product to cart.";
        } catch (ShoppingCartException ex) {
            return ex.getMessage();
        }

    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = customerAuthHelper.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("No authenticated customer");
        }
        return customerService.getCustomerByEmail(email);
    }
    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(@PathVariable("productId") Integer productId, @PathVariable("quantity") Integer quantity, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            float subtotal = cartService.updateQuantity(productId, quantity, customer);

            return String.valueOf(subtotal);
        } catch (CustomerNotFoundException ex) {
            return "You must login to change quantity of product.";
        }
    }

    @DeleteMapping("/cart/remove/{productId}")
    public String removeProduct(@PathVariable("productId") Integer productId, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            cartService.removeProduct(productId, customer);

            return "The product has been removed from your shopping cart.";

        } catch (CustomerNotFoundException e) {
            return "You must login to remove product.";
        }
    }
}

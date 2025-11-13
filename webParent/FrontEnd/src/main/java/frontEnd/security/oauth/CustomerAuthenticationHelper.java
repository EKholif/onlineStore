package frontEnd.security.oauth;


import frontEnd.customer.CustomerService;
import frontEnd.security.CustomerUserDetails;
import com.onlineStoreCom.entity.customer.Customer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CustomerAuthenticationHelper {

    private final CustomerService customerService;

    @Autowired
    public CustomerAuthenticationHelper(CustomerService customerService) {
        this.customerService = customerService;
    }

    public Integer getAuthenticatedCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomerUserDetails userDetails) {
            return userDetails.getId();

        } else if (principal instanceof CustomerOAuth2User oauthUser) {
            String email = oauthUser.getEmail();
            Customer customer = customerService.getCustomerByEmail(email);
            if (customer != null) {
                return customer.getId();
            }
        }

        return null;
    }

    public String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomerUserDetails userDetails) {
            return userDetails.getUsername();

        } else if (principal instanceof CustomerOAuth2User oauthUser) {
            String email = oauthUser.getEmail();
            Customer customer = customerService.getCustomerByEmail(email);
            if (customer != null) {
                return customer.getEmail();
            }
        }

       return null;
    }
}

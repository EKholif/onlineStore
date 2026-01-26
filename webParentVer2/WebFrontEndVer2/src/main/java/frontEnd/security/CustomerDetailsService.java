package frontEnd.security;

import com.onlineStoreCom.entity.customer.Customer;
import frontEnd.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomerDetailsService implements UserDetailsService {

@Autowired
    private CustomerRepository customerRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // AG-SEC-FIX: Enforce Tenant Isolation
        Long currentTenantId = com.onlineStoreCom.tenant.TenantContext.getTenantId();
        
        // Use strict tenant-scoped lookup
        Customer customer = customerRepository.findByEmailAndTenantId(email, currentTenantId);
        
        if (customer == null) {
            // Fallback for logging/debugging (optional, or just throw)
            throw new UsernameNotFoundException("Customer not found in Tenant ID: " + currentTenantId);
        }

        return new CustomerUserDetails(customer);
    }




}

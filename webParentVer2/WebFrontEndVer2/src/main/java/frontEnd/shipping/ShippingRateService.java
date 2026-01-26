package frontEnd.shipping;

import com.onlineStoreCom.entity.address.Address;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingRateService {

    @Autowired
    private ShippingRateRepository repo;

    public ShippingRate getShippingRateForCustomer(Customer customer) {
        String state = customer.getState();

        if (state == null || state.isEmpty()) {
            state = customer.getCity(); // Fallback to City if State is empty
        }
        
        // Normalize state (Trim whitespace)
        if (state != null) {
            state = state.trim();
        }

        // AG-TEN-FIX: Use tenant-scoped query for shipping rates
        Long tenantId = com.onlineStoreCom.tenant.TenantContext.getTenantId();
        
        if (customer.getCountry() == null) {
             System.err.println("❌ ERROR: Customer has no assigned country!");
             return null;
        }

        System.out.println("🔍 SHIPPING DEBUG:");
        System.out.println("   - Customer: " + customer.getEmail());
        System.out.println("   - Tenant ID: " + tenantId);
        System.out.println("   - Country: " + customer.getCountry().getName() + " (ID: " + customer.getCountry().getId() + ")");
        System.out.println("   - State (Used for search): '" + state + "'");

        ShippingRate rate = repo.findByCountryAndStateAndTenantId(customer.getCountry(), state, tenantId);
        
        if (rate != null) {
             System.out.println("   ✅ Rate FOUND: " + rate.getRate() + " (ID: " + rate.getId() + ")");
        } else {
             System.out.println("   ❌ Rate NOT FOUND");
        }

        return rate;
    }

    public ShippingRate getShippingRateForAddress(Address address) {
        String state = address.getState();
        if (state == null || state.isEmpty()) {
            state = address.getCity();
        }
        
        // Normalize state (Trim whitespace)
        if (state != null) {
            state = state.trim();
        }

        // AG-TEN-FIX: Use tenant-scoped query for shipping rates
        Long tenantId = com.onlineStoreCom.tenant.TenantContext.getTenantId();
        
        if (address.getCountry() == null) {
             System.err.println("❌ ERROR: Address has no assigned country!");
             return null;
        }

        System.out.println("🔍 SHIPPING DEBUG (Address):");
        System.out.println("   - Tenant ID: " + tenantId);
        System.out.println("   - Country: " + address.getCountry().getName() + " (ID: " + address.getCountry().getId() + ")");
        System.out.println("   - State (Used for search): '" + state + "'");

        ShippingRate rate = repo.findByCountryAndStateAndTenantId(address.getCountry(), state, tenantId);
        
        if (rate != null) {
             System.out.println("   ✅ Rate FOUND: " + rate.getRate() + " (ID: " + rate.getId() + ")");
        } else {
             System.out.println("   ❌ Rate NOT FOUND");
        }

        return rate;
    }
}

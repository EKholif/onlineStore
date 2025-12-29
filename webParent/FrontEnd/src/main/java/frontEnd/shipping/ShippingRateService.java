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
            state = customer.getCity();
        }

        System.out.println("DEBUG SHIPPING: Customer Country: " + customer.getCountry().getName());
        System.out.println("DEBUG SHIPPING: Customer State/City used for search: '" + state + "'");

        ShippingRate rate = repo.findByCountryAndState(customer.getCountry(), state);
        System.out.println("DEBUG SHIPPING: Rate found: " + (rate != null ? rate.getRate() : "NULL"));

        return rate;
    }

    public ShippingRate getShippingRateForAddress(Address address) {
        String state = address.getState();
        if (state == null || state.isEmpty()) {
            state = address.getCity();
        }

        System.out.println("DEBUG SHIPPING: Address Country: " + address.getCountry().getName());
        System.out.println("DEBUG SHIPPING: Address State/City used for search: '" + state + "'");

        ShippingRate rate = repo.findByCountryAndState(address.getCountry(), state);
        System.out.println("DEBUG SHIPPING: Rate found within address: " + (rate != null ? rate.getRate() : "NULL"));

        return rate;
    }
}

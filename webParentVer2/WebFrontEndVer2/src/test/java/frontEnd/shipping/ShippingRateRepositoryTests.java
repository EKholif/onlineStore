package frontEnd.shipping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.INSTANT;

import com.onlineStoreCom.entity.address.Address;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import com.onlineStoreCom.entity.shipping.ShippingRate;
import frontEnd.customerAddress.AddressRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(value = true)
public class ShippingRateRepositoryTests {
	
	@Autowired private ShippingRateRepository repo;
	@Autowired private AddressRepository addressRepository;


	@Autowired
	EntityManager entityManager;

	@Test
	public void testFindByCountryAndState() {
		Country usa = new Country(39);
		String state = "ON";
		int customerId = 163;

		Customer customer = entityManager.find(Customer.class, customerId);


		Address address = addressRepository.findDefaultByCustomer(customerId);

		List<Address> defaultAddress = addressRepository.findByCustomer(customer);
		List<Address>  address3 = addressRepository.findByCustomer(customer);
            addressRepository.setDefaultAddress(2);
		System.out.println(address3.size() + "      eeeeeeeeeee");

		for (Address a     :address3 ) {
             ShippingRate shippingRate = repo.findByCountryAndState( a.getCountry() ,a.getState());
             if (shippingRate!= null) {
				 System.out.println("  test shippingRate " + shippingRate.getRate());
			 }
			if (shippingRate== null) {
				System.out.println("  test shippingRate " + a.getCountry().getName() + " State   " + a.getState());
			}
//			System.out.println("a.getState()a.getState() " +  a.getState());

			
		}
//		for (Address a     :address3 ) {
//
//
//			System.out.println("  test address " + address);
//			System.out.println("  test address " + address);
//


		}

//		ShippingRate shippingRate = repo.findByCountryAndState(defaultAddress.getCountry(), defaultAddress.getState());
//		ShippingRate shippingRat = repo.findByCountryAndState(defaultAddress.getCountry(), defaultAddress.getState());
//
//		System.out.println( "  test name " + shippingRate.getRate());
//		System.out.println( "  test egs " + shippingRat.getRate());
//		System.out.println( "  test egs " + shippingRat.getCountry().getName());

//		assertThat(shippingRate).isNotNull();
//		System.out.println(shippingRate);
//	}
}

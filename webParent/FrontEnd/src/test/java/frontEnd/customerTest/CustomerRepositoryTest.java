package frontEnd.customerTest;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import frontEnd.customer.CustomerRepository;
import frontEnd.product.ProductRepository;
import frontEnd.setting.repository.CountryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)

public class CustomerRepositoryTest {


    @Autowired
    private CustomerRepository customersRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void ListAllCustomer() {

        List<Customer> customerList = customersRepository.findAll();

        for (Customer order : customerList) {
            if (order.getId() == null) {
                System.out.println("Order ID " + order.getId() + " has missing customer!");
            }
        }


        customerList.forEach(c -> System.out.println( "🔥 test name 🔥" + c.getFullName()));

        assertThat(customerList.size()).isGreaterThan(0);
    }


    @Test
    public void newCustomer() {

        Customer customer = new Customer();
        customer.setFirstName("firstName");
        customer.setLastName("lastName");
        customer.setTenantId(0L);
        customer.setId(32);
        customer.setEmail("eh.a.bkh.olif@gmail.com");
        customer.setPassword("");
        customer.setPhoneNumber("0100000000");
        customer.setPostalCode("123456");
        customer.setAddressLine1("addressLine1");
        customer.setCity("city");
        customer.setState("state");
        customer.setPassword("");
        customersRepository.save(customer);


        customersRepository.save(customer);


//        customerList.forEach(c -> System.out.println( "🔥 test name 🔥" + c.getFullName()));
//
//        assertThat(customerList.size()).isGreaterThan(0);
    }
    @Test
    public void countryTest() {

        List<Country> customerList = countryRepository.findAll();


        customerList.forEach(c -> System.out.println( "🔥 test name 🔥" + c.getName()));

        assertThat(customerList.size()).isGreaterThan(0);
    }

    @Test
    public void testListAllCountries() {

        Iterable<Country> listCountries = countryRepository.findAllByOrderByNameAsc();
        System.out.println("🔥 test started 🔥");
        for (Country country : listCountries) {
            System.out.println(" - name   -" + country.getName());
            System.out.println(" - id   -" + country.getId());
            System.out.println(" - code   -" + country.getCode());
            System.out.println("🔥 test started 🔥");
        }

    }
}
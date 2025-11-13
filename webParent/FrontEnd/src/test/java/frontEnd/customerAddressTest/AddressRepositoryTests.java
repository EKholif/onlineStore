package frontEnd.customerAddressTest;


import com.onlineStoreCom.entity.address.Address;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import frontEnd.customerAddress.AddressRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)
public class AddressRepositoryTests {

    @Autowired
    private AddressRepository repo;

    @Test
    public void testAddNew() {
        Integer customerId = 163;
        Integer countryId = 234; // USA

        Address newAddress = new Address();
        newAddress.setCustomer(new Customer(customerId));
        newAddress.setCountry(new Country(countryId));
        newAddress.setFirstName("Tobie");
        newAddress.setLastName("Abel");
        newAddress.setPhoneNumber("19094644165");
        newAddress.setAddressLine1("4213 Gordon Street");
        newAddress.setAddressLine2("Novak Building");
        newAddress.setCity("Chino");
        newAddress.setState("California");
        newAddress.setPostalCode("91710");

        Address savedAddress = repo.save(newAddress);

        assertThat(savedAddress).isNotNull();
        assertThat(savedAddress.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        Integer customerId = 5;
        List<Address> listAddresses = repo.findByCustomer(new Customer(customerId));
        Assertions.assertThat(listAddresses.size()).isGreaterThan(0);

        listAddresses.forEach(System.out::println);
    }

    @Test
    public void testFindByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 5;

        Address address = repo.findByIdAndCustomer(addressId, customerId);

        assertThat(address).isNotNull();
        System.out.println(address);
    }

    @Test
    public void testUpdate() {
        String addressId = "1";
        String phoneNumber = "646-232-3932";

        Address address = repo.findById(addressId).get();
        address.setPhoneNumber(phoneNumber);

        Address updatedAddress = repo.save(address);
        assertThat(updatedAddress.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    public void testDeleteByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 5;

        repo.deleteByIdAndCustomer(addressId, customerId);

        Address address = repo.findByIdAndCustomer(addressId, customerId);
        assertThat(address).isNull();
    }

    @Test
    public void testSetDefault() {
        Integer addressId = 2;
        String addressId1 = "2";
        repo.setDefaultAddress(addressId);

        Address address = repo.findById(addressId1).get();
        assertThat(address.isDefaultForShipping()).isTrue();
    }

    @Test
    public void testSetNonDefaultAddresses() {
        Integer addressId = 8;
        Integer customerId = 5;
        repo.setNonDefaultForOthers(addressId, customerId);
    }

    @Test
    public void testGetDefault() {
        Integer customerId = 5;

        Address address = repo.findDefaultByCustomer(customerId);
        assertThat(address).isNotNull();
        System.out.println(address);
    }
}

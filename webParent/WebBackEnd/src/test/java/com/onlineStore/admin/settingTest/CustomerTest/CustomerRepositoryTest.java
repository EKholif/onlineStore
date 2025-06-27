package com.onlineStore.admin.settingTest.CustomerTest;

import com.onlineStore.admin.setting.country.CountryRepository;
import com.onlineStore.admin.usersAndCustomers.customer.repository.CustomersRepository;
import com.onlineStoreCom.entity.customer.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)

public class CustomerRepositoryTest {


    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private CountryRepository countryRepository;



   @Test
    public void ListAllCustomer(){

       List<Customer> customerList = customersRepository.findAll();


       customerList.forEach(c -> System.out.println(c.getCountry()));

       assertThat(customerList.size()).isGreaterThan(0);
   }





@Test
   public void TestsavedCustomer (){
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    Customer customer = customersRepository.findByEmail("ehabkholif@gmail.com");
    String rawPassword = "";
    String encodedPassword = customer.getPassword();
    boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
    assertThat(matches).isTrue();


}

}
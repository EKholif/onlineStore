package frontEnd.customer;

import com.onlineStoreCom.entity.AuthenticationType;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.exception.CustomerNotFoundException;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import frontEnd.setting.repository.CountryRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CustomerService {
    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    public CustomerRepository customerRepository;
    @Autowired
    private CountryRepository countryRepository;

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }


    public boolean isEmailUnique(Integer id, String email) {
        Customer customerByEmail = customerRepository.findByEmail(email);

        boolean isCreatingNew = (id == null);

        if (isCreatingNew) {
            // في حالة إنشاء عميل جديد، الإيميل لازم يكون مش موجود قبل كده
            return customerByEmail == null;
        } else {
            // لو الإيميل مش موجود أصلاً، يبقى فريد
            if (customerByEmail == null) return true;

            // لو الإيميل موجود، لكن لنفس الـ ID (يعني بيعدل نفسه)، يبقى فريد برضه
            return customerByEmail.getId().equals(id);
        }
    }

    public Customer saveCustomer(Customer customer) {

        boolean existingCustomer = customer.getId() != null;


        if (existingCustomer) {
            customer.setEnabled(true);

            if (customer.getPassword() == null || customer.getPassword().isEmpty()) {
                Customer saved = customerRepository.getReferenceById(customer.getId());
                customer.setPassword(saved.getPassword());
            } else {
                encodePassword(customer);
            }
            customer.getRestPasswordToken();
            return customerRepository.saveAndFlush(customer);

        } else {
            // New customer
            encodePassword(customer);
            if (customer.getAuthenticationType() == AuthenticationType.DATABASE) {
                String randomCode = RandomStringUtils.randomAlphabetic(64);
                customer.setEnabled(false);
                customer.setVerificationCode(randomCode);

            }
            customer.setCreatedTime(new Date());

        }

        return customerRepository.saveAndFlush(customer);
    }


    public String updaterest_password(String email) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByEmail(email);

        if (customer != null) {
            String token = RandomStringUtils.randomAlphabetic(30);
            customer.setRestPasswordToken(token);
            customerRepository.save(customer);

            return token;
        } else {
            throw new CustomerNotFoundException("Could not find any customer with the email " + email);
        }
    }

    public Customer saveUpdatededUser(Customer customer) {
        return customerRepository.saveAndFlush(customer);
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    private void encodePassword(Customer customer) {
        String encodePassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodePassword);

    }

    public boolean verify(String verificationCode) {
        Customer customer = customerRepository.findByVerificationCode(verificationCode);

        if (customer == null || customer.isEnabled()) {
            return false;
        } else {
            customerRepository.enable(customer.getId());
            return true;
        }
    }

    public Customer registerCustomer(Customer customer) {
        String rawPassword = customer.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        customer.setPassword(encodedPassword);
        return customerRepository.save(customer);
    }


    public Customer findById(Integer id) {
        return customerRepository.getReferenceById(id);
    }

    public String getFullName(Integer id) {
        return findById(id).getFullName();
    }


    public void updateAuthenticationType(Customer customer, AuthenticationType type, String countryCode) {


        Country newCountry = countryRepository.findByCode(countryCode);
        Country existingCountry = customer.getCountry();

        if (existingCountry == null || !existingCountry.getName().equals(newCountry.getName())) {
            customerRepository.updateCountryCode(customer.getId(), newCountry);
        }

        if (customer.getAuthenticationType() == null || !customer.getAuthenticationType().equals(type)) {
            customerRepository.updateAuthenticationType(customer.getId(), type);
        }


    }

    public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode, AuthenticationType authenticationType, String picture) {
        Customer customer = new Customer();
        customer.setEmail(email);
        setName(name, customer);
        customer.setImage(picture);

        customer.setEnabled(true);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(authenticationType);
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setCity("");
        customer.setState("");
        customer.setPhoneNumber("");
        customer.setPostalCode("");
        customer.setCountry(countryRepository.findByCode(countryCode));
        saveCustomer(customer);
    }


    private void setName(String name, Customer customer) {
        String[] nameArray = name.split(" ");
        if (nameArray.length < 2) {
            customer.setFirstName(name);
            customer.setLastName("");
        } else {
            String firstName = nameArray[0];
            customer.setFirstName(firstName);

            String lastName = name.replaceFirst(firstName + " ", "");
            customer.setLastName(lastName);
        }
    }

    public Customer getByrest_password(String token) {
        return customerRepository.findByrest_password(token);
    }

    public void updatePassword(String token, String newPassword) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByrest_password(token);
        if (customer == null) {
            throw new CustomerNotFoundException("No customer found: invalid token");
        }

        customer.setPassword(newPassword);
        customer.setRestPasswordToken(null);
        encodePassword(customer);

        customerRepository.save(customer);
    }

}

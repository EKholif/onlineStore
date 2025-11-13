package frontEnd.customer;

import com.onlineStoreCom.entity.AuthenticationType;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("SELECT c FROM Customer c WHERE c.enabled = true AND c.email = ?1")
    Customer findByEmail( String email);

    @Query("SELECT c FROM Customer c WHERE  c.restPasswordToken = ?1")
    Customer findByrest_password( String rest_password);



    boolean existsByEmail(String email);
    Customer findByVerificationCode ( String VerificationCode);

    @Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null WHERE c.id = ?1")
    @Modifying
    void enable(Integer id);



    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.id = ?1")
    @Modifying
    void updateAuthenticationType(Integer id, AuthenticationType type);



    @Query("UPDATE Customer c SET c.country = ?2 WHERE c.id = ?1")
    @Modifying
    void updateCountryCode(Integer id, Country country);





}



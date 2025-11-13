package frontEnd.security;

import com.onlineStoreCom.entity.customer.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomerUserDetails implements UserDetails {

    private final Customer customer;

    public CustomerUserDetails(Customer customer) {
        this.customer = customer;

    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    }


    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        return customer.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return customer.isEnabled();
    }




    public String getUserEmail() {
        return customer.getEmail();
    }

    public String getImagePath() {

        return customer.getImagePath();
    }

    public String picture() {

        return customer.getImagePath();
    }

    public Integer getId() {
        return customer.getId();
    }

    public String getFullName() {
        return customer.getFirstName() + " "+ customer.getLastName();
    }

}
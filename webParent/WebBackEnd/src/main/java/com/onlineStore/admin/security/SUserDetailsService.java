package com.onlineStore.admin.security;

import com.onlineStore.admin.user.UserRepository;
import com.onlineStoreCom.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class SUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user != null) {

            return new StoreUserDetails(user);
        }

        throw new UsernameNotFoundException(" User Not  Found");
    }
}

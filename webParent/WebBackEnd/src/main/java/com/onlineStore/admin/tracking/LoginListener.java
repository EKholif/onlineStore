package com.onlineStore.admin.tracking;

import com.onlineStore.admin.security.StoreUserDetails;
import com.onlineStore.admin.usersAndCustomers.users.UserRepository;
import com.onlineStoreCom.entity.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class LoginListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private ActivityTracker activityTracker;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof StoreUserDetails) {
            StoreUserDetails userDetails = (StoreUserDetails) principal;
            User user = userDetails.getUser();

            // 1. Update In-Memory Tracker
            activityTracker.userLoggedIn(user.getId());

            // 2. Update Database LastLoginTime
            user.setLastLoginTime(new Date());
            userRepository.save(user);
        }
    }
}

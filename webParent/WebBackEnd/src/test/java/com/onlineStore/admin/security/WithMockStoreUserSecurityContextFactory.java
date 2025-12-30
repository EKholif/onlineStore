package com.onlineStore.admin.security;

import com.onlineStoreCom.entity.users.Role;
import com.onlineStoreCom.entity.users.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockStoreUserSecurityContextFactory implements WithSecurityContextFactory<WithMockStoreUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockStoreUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = new User();
        user.setEmail(customUser.username());
        user.setPassword(customUser.password());
        user.setUser_bio(null); // Ensure no null pointer if accessed
        user.setTenantId(1L);

        for (String roleName : customUser.roles()) {
            user.addRole(new Role(roleName, "Mock Role"));
        }

        StoreUserDetails principal = new StoreUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
                principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}

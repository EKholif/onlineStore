package com.onlineStore.admin.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockStoreUserSecurityContextFactory.class)
public @interface WithMockStoreUser {

    String username() default "admin";

    String password() default "password";

    String[] roles() default {"Admin"};
}

package com.onlineStore.admin;

import com.onlineStore.admin.usersAndCustomers.users.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@WebMvcTest(value = MainController.class, excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = {
        com.onlineStore.admin.security.tenant.TenantContextFilter.class, com.onlineStore.admin.JpaConfig.class}))
@org.springframework.context.annotation.Import(com.onlineStore.admin.security.WebSecurityConfig.class)
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private com.onlineStore.admin.usersAndCustomers.users.UserRepository userRepository;

    @MockBean
    private com.onlineStore.admin.security.tenant.CustomLoginSuccessHandler customLoginSuccessHandler;

    @MockBean
    private com.onlineStore.admin.security.tenant.TenantContextFilter tenantContextFilter;

    @BeforeEach
    public void setup() throws Exception {
        doAnswer(invocation -> {
            ((FilterChain) invocation.getArgument(2)).doFilter(
                    invocation.getArgument(0),
                    invocation.getArgument(1));
            return null;
        }).when(tenantContextFilter).doFilter(any(ServletRequest.class), any(ServletResponse.class),
                any(FilterChain.class));
    }

    @Test
    @WithMockUser // Simulate authenticated user
    public void testViewHome_Authenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isFound()) // 302 Redirect
                .andExpect(redirectedUrl("/users/users"));
    }

    @Test
    @WithAnonymousUser
    public void testViewLoginPage_Anonymous() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithMockUser
    public void testViewLoginPage_Authenticated() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));
    }
}

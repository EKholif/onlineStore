package com.onlineStore.admin.usersAndCustomers.users.controller;

import com.onlineStore.admin.usersAndCustomers.users.servcies.UserService;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * AG-BACK-USER-001: Testing User Administration.
 * Critical Business Path: Ensures admin can list and manage system users.
 */
@WebMvcTest(value = UserController.class, excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = com.onlineStore.admin.security.tenant.TenantContextFilter.class))
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private EntityManager entityManager;

    @Test
    @WithMockUser(username = "admin", roles = {"Admin"})
    public void testListAllUsers() throws Exception {
        // Arrange
        Session session = Mockito.mock(Session.class);
        Mockito.when(entityManager.unwrap(Session.class)).thenReturn(session);
        // We Mock getEnabledFilter to return null or a mock filter if needed
        Mockito.when(session.getEnabledFilter("tenantFilter")).thenReturn(null);

        // Act & Assert
        // The controller calls listByPage(1, ...) which returns a view
        mockMvc.perform(get("/users/users"))
                // Note: Controller returns a ModelAndView forwarding to listByPage, or directly
                // returns view "users/users"
                // In this specific Controller logic (UserController.java:53), it returns
                // listByPage(...) result.
                // listByPage returns "users/users".
                .andExpect(status().isOk())
                .andExpect(view().name("users/users"));
    }
}

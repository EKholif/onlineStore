package com.onlineStore.admin.order;

import com.onlineStore.admin.product.service.ProductService;
import com.onlineStore.admin.setting.service.SettingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * AG-BACK-ORDER-001: Testing Order Management access.
 * Critical Business Path: Ensures admins and permitted roles can access and
 * manage orders.
 */
@WebMvcTest(value = OrderController.class, excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = com.onlineStore.admin.security.tenant.TenantContextFilter.class))
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private SettingService settingService;

    @MockBean
    private ProductService productService;

    @Test
    @WithMockUser(username = "admin", roles = {"Admin"})
    public void testListFirstPage() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isFound()) // 302 Redirect
                .andExpect(view().name("redirect:/orders/page/1?sortField=orderTime&sortDir=desc"));
    }
}

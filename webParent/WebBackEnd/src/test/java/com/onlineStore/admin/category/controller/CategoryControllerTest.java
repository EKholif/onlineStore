package com.onlineStore.admin.category.controller;

import com.onlineStore.admin.category.services.CategoryService;
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
 * AG-BACK-CAT-001: Testing Category Management.
 * Business Path: Admin catalog classification.
 */
@WebMvcTest(value = CategoryController.class, excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = com.onlineStore.admin.security.tenant.TenantContextFilter.class))
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;
    @MockBean
    private SettingService settingService;

    @Test
    @WithMockUser(username = "admin", roles = {"Admin"})
    public void testListAllCategories() throws Exception {
        mockMvc.perform(get("/categories/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/categories"));
    }
}

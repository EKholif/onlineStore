package com.onlineStore.admin.product;

import com.onlineStore.admin.brand.BrandService;
import com.onlineStore.admin.category.services.CategoryService;
import com.onlineStore.admin.product.service.ProductService;
import com.onlineStore.admin.setting.service.SettingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * AG-BACK-PROD-001: Testing Product Management.
 * Business Path: Admin catalog management.
 */
@WebMvcTest(value = ProductController.class, excludeFilters = @org.springframework.context.annotation.ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE, classes = {
        com.onlineStore.admin.security.tenant.TenantContextFilter.class, com.onlineStore.admin.JpaConfig.class}))
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;
    @MockBean
    private BrandService brandService;
    @MockBean
    private CategoryService categoryService;
    @MockBean
    private SettingService settingService;

    @MockBean(name = "entityManagerFactory")
    private jakarta.persistence.EntityManagerFactory entityManagerFactory;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        com.onlineStoreCom.tenant.TenantContext.setTenantId(1L);
    }

    @Test
    @com.onlineStore.admin.security.WithMockStoreUser(username = "admin", roles = {"Admin"})
    public void testListAllProducts() throws Exception {
        when(productService.listAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/products/products"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products/page/1?sortField=name&sortDir=asc"));
    }
}

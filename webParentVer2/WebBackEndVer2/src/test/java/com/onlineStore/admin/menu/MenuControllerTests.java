package com.onlineStore.admin.menu;

import com.onlineStore.admin.article.ArticleService;
import com.onlineStoreCom.entity.menu.Menu;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MenuControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuService service;

    @MockBean
    private ArticleService articleService; // Mocked dependencies

    @Test
    @WithMockUser(username = "admin", roles = {"Admin", "Editor"})
    public void testListAll() throws Exception {
        when(service.listAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/menus"))
                .andExpect(status().isOk())
                .andExpect(view().name("menus/menu_items"))
                .andExpect(model().attributeExists("listMenuItems"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"Admin", "Editor"})
    public void testNewMenu() throws Exception {
        mockMvc.perform(get("/menus/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("menus/menu_form"))
                .andExpect(model().attributeExists("menu"))
                .andExpect(model().attributeExists("listArticles"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"Admin", "Editor"})
    public void testSaveMenu() throws Exception {
        doNothing().when(service).save(any(Menu.class));

        mockMvc.perform(post("/menus/save")
                        .param("title", "Test Menu")
                        .param("alias", "test-menu"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/menus"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"Admin", "Editor"})
    public void testEditMenu_Found() throws Exception {
        Menu menu = new Menu();
        menu.setId(1);
        when(service.get(1)).thenReturn(menu);

        mockMvc.perform(get("/menus/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("menus/menu_form"))
                .andExpect(model().attributeExists("menu"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"Admin", "Editor"})
    public void testEditMenu_NotFound() throws Exception {
        when(service.get(1)).thenThrow(new MenuNotFoundException("Not found"));

        mockMvc.perform(get("/menus/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/menus"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"Admin", "Editor"})
    public void testDeleteMenu_Found() throws Exception {
        doNothing().when(service).delete(1);

        mockMvc.perform(get("/menus/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/menus"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"Admin", "Editor"})
    public void testDeleteMenu_NotFound() throws Exception {
        doThrow(new MenuNotFoundException("Not found")).when(service).delete(1);

        mockMvc.perform(get("/menus/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/menus"));
    }
}

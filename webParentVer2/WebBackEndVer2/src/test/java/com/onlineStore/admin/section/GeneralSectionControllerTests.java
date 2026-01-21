package com.onlineStore.admin.section;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GeneralSectionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SectionService service;

    @Test
    @WithMockUser(username = "admin", roles = {"Admin", "Editor"})
    public void testListAllSections() throws Exception {
        when(service.listSections()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/sections"))
                .andExpect(status().isOk())
                .andExpect(view().name("sections/sections"))
                .andExpect(model().attributeExists("listSections"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"Admin"})
    public void testDeleteSection_Success() throws Exception {
        doNothing().when(service).deleteSection(1);

        mockMvc.perform(get("/sections/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sections"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"Admin"})
    public void testDeleteSection_NotFound() throws Exception {
        doThrow(new SectionNotFoundException("Check Id")).when(service).deleteSection(1);

        mockMvc.perform(get("/sections/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sections"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"Admin"})
    public void testUpdateSectionEnabledStatus() throws Exception {
        doNothing().when(service).updateSectionEnabledStatus(1, true);

        mockMvc.perform(get("/sections/1/enabled/true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sections"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"Admin"})
    public void testMoveSectionUp_Success() throws Exception {
        doNothing().when(service).moveSectionUp(1);

        mockMvc.perform(get("/sections/moveup/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sections"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"Admin"})
    public void testMoveSectionDown_Success() throws Exception {
        doNothing().when(service).moveSectionDown(1);

        mockMvc.perform(get("/sections/movedown/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/sections"));
    }
}

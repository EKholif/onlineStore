package com.onlineStore.admin.test.counteryTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlineStoreCom.entity.setting.Country.Country;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc


public class CounteryRestControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    EntityManager entityManager;


    @Test
    @WithMockUser(username = "ehab", password = "", roles = "Admin")
    public void countryListTest() throws Exception {

        String url = "/setting/list";
        MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jisonResponse = result.getResponse().getContentAsString();
        Country[] country = objectMapper.readValue(jisonResponse, Country[].class);
        assertThat(country).hasSizeGreaterThan(0);

    }


    @Test
    @WithMockUser(username = "ehab", password = "", roles = "Admin")
    public void saveCountryTest() throws Exception {


        String url = "/setting/save";
        String countryName = "Canada";
        String countryCode = "CA";

        Country country = new Country(countryName, countryCode);
        MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Integer countryID = Integer.parseInt(jsonResponse);

        Country saveCountry = entityManager.find(Country.class, countryID);

        assertThat(saveCountry.getName()).isEqualTo(countryName);


    }

    @Test
    @WithMockUser(username = "ehab@online.com", password = "", roles = "Admin")
    public void testUpdateCountry() throws Exception {
        String url = "/countries/save";

        Integer countryId = 6;
        String countryName = "China";
        String countryCode = "CN";
        Country country = new Country(countryId, countryName, countryCode);

        mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(countryId)));

        Country findById = entityManager.find(Country.class, countryId);


        Assertions.assertThat(findById).isNotNull();


        assertThat(findById.getName()).isEqualTo(countryName);
    }

    @Test
    @WithMockUser(username = "ehab@online.com", password = "", roles = "Admin")
    public void testDeleteCountry() throws Exception {
        Integer countryId = 3;
        String url = "/countries/delete/" + countryId;

        mockMvc.perform(delete(url).with(csrf())).andExpect(status().isOk());
        Country findById = entityManager.find(Country.class, countryId);
        Assertions.assertThat(findById).isNull();
    }



}

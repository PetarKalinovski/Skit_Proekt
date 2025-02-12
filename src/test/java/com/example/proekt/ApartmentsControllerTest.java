package com.example.proekt;

import com.example.proekt.model.*;
import com.example.proekt.service.ApartmentService;
import com.example.proekt.web.ApartmentsController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ApartmentsController.class)
public class ApartmentsControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private ApartmentService apartmentService;

    private Apartment testApartment;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Initialize test data
        testUser = new User("testuser", "password", Role.ROLE_USER);

        testApartment = new Apartment();
        testApartment.setId(1L);
        testApartment.setTitle("Test Apartment");
        testApartment.setMunicipality(MunicipalityType.Center);
        testApartment.setAddress("Test Address");
        testApartment.setNumRooms(2);
        testApartment.setSize(75);
        testApartment.setOwner(testUser);
        testApartment.setImageUrls(Arrays.asList("image1.jpg", "image2.jpg"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testCreateApartment() throws Exception {
        when(apartmentService.create(
                eq(MunicipalityType.Center),
                eq("Test Address"),
                eq(2),
                eq(75),
                any(List.class),
                eq("Test Apartment"),
                eq("testuser")
        )).thenReturn(testApartment);

        mockMvc.perform(post("/apart")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("municipality", "Center")
                        .param("address", "Test Address")
                        .param("numRooms", "2")
                        .param("size", "75")
                        .param("imageUrls", "image1.jpg", "image2.jpg")
                        .param("title", "Test Apartment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apartments"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testEditApartment() throws Exception {
        when(apartmentService.findById(1L)).thenReturn(testApartment);
        when(apartmentService.update(
                eq(1L),
                eq(MunicipalityType.Aerodrom),
                eq("New Address"),
                eq(3),
                eq(90),
                any(List.class),
                eq("Updated Apartment")
        )).thenReturn(testApartment);

        mockMvc.perform(post("/apart/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("municipality", "Aerodrom")
                        .param("address", "New Address")
                        .param("numRooms", "3")
                        .param("size", "90")
                        .param("imageUrls", "newimage1.jpg", "newimage2.jpg")
                        .param("title", "Updated Apartment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apartments"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testDeleteApartment() throws Exception {
        when(apartmentService.delete(1L)).thenReturn(testApartment);

        mockMvc.perform(post("/apartments/delete/apt/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apartments"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testShowAddApartmentForm() throws Exception {
        mockMvc.perform(get("/apart/add/apt")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("apartForm"))
                .andExpect(model().attributeExists("municipalities"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testShowEditApartmentForm() throws Exception {
        when(apartmentService.findById(1L)).thenReturn(testApartment);

        mockMvc.perform(get("/apart/edit/apt/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("apartForm"))
                .andExpect(model().attributeExists("apa"))
                .andExpect(model().attributeExists("municipalities"));
    }
}
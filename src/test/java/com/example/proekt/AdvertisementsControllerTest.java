package com.example.proekt;

import com.example.proekt.model.*;
import com.example.proekt.service.AdvertisementService;
import com.example.proekt.service.ApartmentService;
import com.example.proekt.service.MessageThreadService;
import com.example.proekt.service.UserService;
import com.example.proekt.web.AdvertismentsController;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdvertismentsController.class)
public class AdvertisementsControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private AdvertisementService advertisementService;

    @MockBean
    private ApartmentService apartmentService;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageThreadService messageThreadService;

    private Advertisement testAdvertisement;
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
        testApartment.setImageUrls(Arrays.asList("test-image-1.jpg", "test-image-2.jpg"));

        testAdvertisement = new Advertisement();
        testAdvertisement.setId(1L);
        testAdvertisement.setApartment(testApartment);
        testAdvertisement.setType(AdvertisementType.SELL);
        testAdvertisement.setPrice(100000.0);
        testAdvertisement.setOwner(testUser);


        Map<String, Double> ratings = new HashMap<>();
        ratings.put("user1", 4.0);
        ratings.put("user2", 5.0);
        testAdvertisement.setRatings(ratings);

        // Set up common mock behaviors
        when(userService.findByUsername("testuser")).thenReturn(testUser);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testListAllAdvertisements() throws Exception {
        List<Advertisement> advertisements = Arrays.asList(testAdvertisement);
        when(advertisementService.filter(null, null, null, null,
                null, null, null, null, null, null))
                .thenReturn(advertisements);

        when(advertisementService.minSize()).thenReturn(75);
        when(advertisementService.maxSize()).thenReturn(150);
        when(advertisementService.minPrice()).thenReturn(100000.0);
        when(advertisementService.maxPrice()).thenReturn(200000.0);

        mockMvc.perform(get("/apartments")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("list"))
                .andExpect(model().attributeExists("ads"))
                .andExpect(model().attribute("ads", advertisements))
                .andExpect(model().attributeExists("smallSize"))
                .andExpect(model().attributeExists("bigSize"))
                .andExpect(model().attributeExists("smallPrice"))
                .andExpect(model().attributeExists("bigPrice"))
                .andExpect(model().attributeExists("municipalities"))
                .andExpect(model().attributeExists("types"));
        }

        @Test
        @WithMockUser(username = "testuser", roles = "USER")
        void testCreateAdvertisement() throws Exception {
            when(advertisementService.create(eq(1L), eq(AdvertisementType.SELL),
                    eq(100000.0), eq("testuser")))
                    .thenReturn(testAdvertisement);

            mockMvc.perform(post("/apartments")
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .param("apartment", "1")
                            .param("type", "SELL")
                            .param("price", "100000.0"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/apartments"));
        }

        @Test
        @WithMockUser(username = "testuser", roles = "USER")
        void testEditAdvertisement() throws Exception {
            when(advertisementService.findById(1L)).thenReturn(testAdvertisement);
            when(advertisementService.update(eq(1L), eq(1L), eq(AdvertisementType.RENT),
                    eq(90000.0)))
                    .thenReturn(testAdvertisement);

            mockMvc.perform(post("/apartments/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .param("apartment", "1")
                            .param("type", "RENT")
                            .param("price", "90000.0"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/apartments"));
        }

        @Test
        @WithMockUser(username = "testuser", roles = "USER")
        void testDeleteAdvertisement() throws Exception {
            when(advertisementService.delete(1L)).thenReturn(testAdvertisement);
            when(messageThreadService.deleteByAdvertisement(1L)).thenReturn(Arrays.asList());

            mockMvc.perform(post("/apartments/delete/ad/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/apartments"));
        }

        @Test
        @WithMockUser(username = "testuser", roles = "USER")
        void testAddRating() throws Exception {
            when(advertisementService.addRating(4.5, 1L, "testuser"))
                    .thenReturn(testAdvertisement);

            mockMvc.perform(post("/apartments/rate/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .param("rating", "4.5"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/apartments/details/1"));
        }

        @Test
        @WithMockUser(username = "testuser", roles = "USER")
        void testAddComment() throws Exception {
            when(advertisementService.addComment("Great apartment!", 1L))
                    .thenReturn(testAdvertisement);

            mockMvc.perform(post("/apartments/comments/{id}", 1L)
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .param("comment", "Great apartment!"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/apartments/details/1"));
        }
    }
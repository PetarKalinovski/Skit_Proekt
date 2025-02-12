package com.example.proekt;

import com.example.proekt.model.*;
import com.example.proekt.service.AdvertisementService;
import com.example.proekt.service.ApartmentService;
import com.example.proekt.service.MessageThreadService;
import com.example.proekt.service.UserService;
import com.example.proekt.web.AdvertismentsController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AdvertismentsController.class})
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

    private AdvertismentsController controller;

    @BeforeEach
    void setUp() {
        controller= new AdvertismentsController(advertisementService,apartmentService,userService,messageThreadService);

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
    void whenFilteringAds_thenAllFilterParametersAreCorrectlyApplied() throws Exception {
        ArgumentCaptor<Double> priceMoreCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Double> priceLessCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<MunicipalityType> municipalityCaptor = ArgumentCaptor.forClass(MunicipalityType.class);

        when(advertisementService.filter(
                priceMoreCaptor.capture(),
                priceLessCaptor.capture(),
                municipalityCaptor.capture(),
                any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(Collections.singletonList(testAdvertisement));

        mockMvc.perform(get("/apartments")
                        .param("priceMore", "50000.0")
                        .param("priceLess", "150000.0")
                        .param("municipality", "Center")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());

        assertEquals(50000.0, priceMoreCaptor.getValue());
        assertEquals(150000.0, priceLessCaptor.getValue());
        assertEquals(MunicipalityType.Center, municipalityCaptor.getValue());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void createAd() throws Exception {
        ArgumentCaptor<Long> apartmentIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<AdvertisementType> typeCaptor = ArgumentCaptor.forClass(AdvertisementType.class);
        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);

        when(advertisementService.create(
                apartmentIdCaptor.capture(),
                typeCaptor.capture(),
                priceCaptor.capture(),
                usernameCaptor.capture()
        )).thenReturn(testAdvertisement);

        mockMvc.perform(post("/apartments")
                        .param("apartment", "1")
                        .param("type", "SELL")
                        .param("price", "100000.0")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection());

        assertEquals(1L, apartmentIdCaptor.getValue());
        assertEquals(AdvertisementType.SELL, typeCaptor.getValue());
        assertEquals(100000.0, priceCaptor.getValue());
        assertEquals("testuser", usernameCaptor.getValue());

        // Verify service was called exactly once
        verify(advertisementService, times(1)).create(any(), any(), any(), any());
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


//    @Test
//    @WithMockUser(username = "testuser", roles = "USER")
//    void editAdWrongUser() throws Exception {
//        User differentUser = new User("different", "pwd", Role.ROLE_USER);
//        testAdvertisement.setOwner(differentUser);
//
//        when(advertisementService.findById(1L)).thenReturn(testAdvertisement);
//        when(userService.findByUsername("testuser")).thenReturn(testUser);
//
//        mockMvc.perform(post("/apartments/1")
//                        .param("apartment", "1")
//                        .param("type", "RENT")
//                        .param("price", "90000.0")
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccessDeniedException))
//                .andExpect(result -> assertEquals(
//                        "You do not have permission to edit this advertisement",
//                        result.getResolvedException().getMessage()
//                ));
//
//
//        verify(advertisementService, never()).update(any(), any(), any(), any());
//    }

    @Test
    void editAdWrongUser() {
        User differentUser = new User("different", "pwd", Role.ROLE_USER);
        testAdvertisement.setOwner(differentUser);

        when(advertisementService.findById(1L)).thenReturn(testAdvertisement);
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        Principal principal = () -> "testuser";

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                controller.editAd(1L, 1L, AdvertisementType.RENT, 90000.0, principal)
        );

        assertEquals("You do not have permission to edit this advertisement", exception.getMessage());
        verify(advertisementService, never()).update(any(), any(), any(), any());
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
    void testAddInvalidRating() throws Exception {
        mockMvc.perform(post("/apartments/rate/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("rating", "6.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apartments/details/1"));

        verify(advertisementService, never()).addRating(any(), any(), any());
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

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testAddEmptyComment() throws Exception {
        mockMvc.perform(post("/apartments/comments/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("comment", "   "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apartments/details/1"));

        verify(advertisementService, never()).addComment(any(), any());
    }
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testShowAdd() throws Exception {
        List<Apartment> apartments = Arrays.asList(testApartment);
        when(apartmentService.listAll()).thenReturn(apartments);

        mockMvc.perform(get("/apartments/add/ad")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("adForm"))
                .andExpect(model().attributeExists("apartments"))
                .andExpect(model().attributeExists("municipalities"))
                .andExpect(model().attributeExists("types"))
                .andExpect(model().attribute("user", "testuser"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testShowEdit() throws Exception {
        when(advertisementService.findById(1L)).thenReturn(testAdvertisement);
        when(apartmentService.listAll()).thenReturn(Arrays.asList(testApartment));

        mockMvc.perform(get("/apartments/edit/ad/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("adForm"))
                .andExpect(model().attributeExists("ad"))
                .andExpect(model().attributeExists("apartments"))
                .andExpect(model().attributeExists("municipalities"))
                .andExpect(model().attributeExists("types"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testDetailsApartments_NewMessageThread() throws Exception {
        when(advertisementService.findById(1L)).thenReturn(testAdvertisement);
        when(apartmentService.listAll()).thenReturn(Arrays.asList(testApartment));
        when(messageThreadService.findByUser1AndUser2AndAdvertisement("testuser", "testuser", 1L))
                .thenReturn(null);

        MessageThread newThread = new MessageThread();
        newThread.setId(1L);
        when(messageThreadService.create("testuser", "testuser", 1L)).thenReturn(newThread);

        mockMvc.perform(get("/apartments/details/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("details"))
                .andExpect(model().attributeExists("ad"))
                .andExpect(model().attributeExists("apartments"))
                .andExpect(model().attributeExists("municipalities"))
                .andExpect(model().attributeExists("types"))
                .andExpect(model().attribute("threadId", 1L));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testDetailsApartments_ExistingMessageThread() throws Exception {
        when(advertisementService.findById(1L)).thenReturn(testAdvertisement);
        when(apartmentService.listAll()).thenReturn(Arrays.asList(testApartment));

        MessageThread existingThread = new MessageThread();
        existingThread.setId(1L);
        when(messageThreadService.findByUser1AndUser2AndAdvertisement("testuser", "testuser", 1L))
                .thenReturn(existingThread);

        mockMvc.perform(get("/apartments/details/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("details"))
                .andExpect(model().attributeExists("ad"))
                .andExpect(model().attribute("threadId", 1L));
    }

}
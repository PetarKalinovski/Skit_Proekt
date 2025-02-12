package com.example.proekt;

import com.example.proekt.ProektApplication;
import com.example.proekt.model.Apartment;
import com.example.proekt.model.MunicipalityType;
import com.example.proekt.model.User;
import com.example.proekt.model.Role;
import com.example.proekt.repository.ApartmentReopository;
import com.example.proekt.service.ApartmentService;
import com.example.proekt.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ProektApplication.class)
@ActiveProfiles("test")
@Transactional
class ApartmentServiceTest {

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApartmentReopository apartmentRepository;

    private User testUser;
    private Apartment testApartment;

    @BeforeEach
    void setUp() {
        apartmentRepository.deleteAll();

        testUser = userService.register("testuser", "password", "password", Role.ROLE_USER);

        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("test-image-1.jpg");
        imageUrls.add("test-image-2.jpg");

        testApartment = apartmentService.create(
                MunicipalityType.Center,
                "Test Address",
                3,
                100,
                imageUrls,
                "Test Apartment",
                testUser.getUsername()
        );
    }

    @Test
    void testCreateApartment() {
        assertNotNull(testApartment.getId());
        assertEquals("Test Address", testApartment.getAddress());
        assertEquals(MunicipalityType.Center, testApartment.getMunicipality());
        assertEquals(3, testApartment.getNumRooms());
        assertEquals(100, testApartment.getSize());
        assertEquals("Test Apartment", testApartment.getTitle());
        assertEquals(testUser, testApartment.getOwner());
        assertEquals(2, testApartment.getImageUrls().size());
    }

    @Test
    void testUpdateApartment() {
        List<String> newImageUrls = new ArrayList<>();
        newImageUrls.add("new-image-1.jpg");

        Apartment updatedApartment = apartmentService.update(
                testApartment.getId(),
                MunicipalityType.Aerodrom,
                "Updated Address",
                4,
                120,
                newImageUrls,
                "Updated Title"
        );

        assertEquals("Updated Address", updatedApartment.getAddress());
        assertEquals(MunicipalityType.Aerodrom, updatedApartment.getMunicipality());
        assertEquals(4, updatedApartment.getNumRooms());
        assertEquals(120, updatedApartment.getSize());
        assertEquals("Updated Title", updatedApartment.getTitle());
        assertTrue(updatedApartment.getImageUrls().contains("new-image-1.jpg"));

        Apartment persistedApartment = apartmentService.findById(updatedApartment.getId());
        assertEquals("Updated Address", persistedApartment.getAddress());
        assertEquals(MunicipalityType.Aerodrom, persistedApartment.getMunicipality());
    }

    @Test
    void testListAll() {
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("image3.jpg");

        apartmentService.create(
                MunicipalityType.Butel,
                "Second Address",
                2,
                80,
                imageUrls,
                "Second Apartment",
                testUser.getUsername()
        );

        List<Apartment> apartments = apartmentService.listAll();
        assertEquals(2, apartments.size());
    }

    @Test
    void testDeleteApartment() {
        Long apartmentId = testApartment.getId();

        Apartment deletedApartment = apartmentService.delete(apartmentId);

        assertEquals(testApartment.getId(), deletedApartment.getId());
        assertTrue(apartmentRepository.findById(apartmentId).isEmpty());
        assertEquals(0, apartmentService.listAll().size());
    }

    @Test
    void testFindById() {
        Apartment foundApartment = apartmentService.findById(testApartment.getId());

        assertNotNull(foundApartment);
        assertEquals(testApartment.getId(), foundApartment.getId());
        assertEquals(testApartment.getAddress(), foundApartment.getAddress());
        assertEquals(testApartment.getMunicipality(), foundApartment.getMunicipality());
    }
}
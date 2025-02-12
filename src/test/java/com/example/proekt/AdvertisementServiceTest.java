package com.example.proekt;

import com.example.proekt.ProektApplication;
import com.example.proekt.model.*;
import com.example.proekt.repository.AdvertisementRepository;
import com.example.proekt.service.AdvertisementService;
import com.example.proekt.service.ApartmentService;
import com.example.proekt.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ProektApplication.class)
@ActiveProfiles("test")
@Transactional
class AdvertisementServiceTest {

    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdvertisementRepository advertisementRepository;

    private User testUser;
    private Apartment testApartment;
    private Advertisement testAdvertisement;

    @BeforeEach
    void setUp() {
        advertisementRepository.deleteAll();

        testUser = userService.register("testuser", "password", "password", Role.ROLE_USER);

        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("test-image-1.jpg");

        testApartment = apartmentService.create(
                MunicipalityType.Center,
                "Test Address",
                3,
                100,
                imageUrls,
                "Test Apartment",
                testUser.getUsername()
        );

        testAdvertisement = advertisementService.create(
                testApartment.getId(),
                AdvertisementType.SELL,
                100000.0,
                testUser.getUsername()
        );
        testAdvertisement.setRatings(new HashMap<>());
        testAdvertisement.setComments(new ArrayList<>());
    }

    @Test
    void testCreateAdvertisement() {
        assertNotNull(testAdvertisement.getId());
        assertEquals(testApartment, testAdvertisement.getApartment());
        assertEquals(AdvertisementType.SELL, testAdvertisement.getType());
        assertEquals(100000.0, testAdvertisement.getPrice());
        assertEquals(testUser, testAdvertisement.getOwner());

        Advertisement persistedAd = advertisementService.findById(testAdvertisement.getId());
        assertEquals(AdvertisementType.SELL, persistedAd.getType());
    }

    @Test
    void testUpdateAdvertisement() {
        Advertisement updatedAd = advertisementService.update(
                testAdvertisement.getId(),
                testApartment.getId(),
                AdvertisementType.RENT,
                90000.0
        );

        assertEquals(AdvertisementType.RENT, updatedAd.getType());
        assertEquals(90000.0, updatedAd.getPrice());

        Advertisement persistedAd = advertisementService.findById(updatedAd.getId());
        assertEquals(AdvertisementType.RENT, persistedAd.getType());
        assertEquals(90000.0, persistedAd.getPrice());
    }

    @Test
    void testAddRating() {
        Advertisement ratedAd = advertisementService.addRating(4.5, testAdvertisement.getId(), "testuser");

        assertTrue(ratedAd.getRatings().containsKey("testuser"));
        assertEquals(4.5, ratedAd.getRatings().get("testuser"));

        Advertisement persistedAd = advertisementService.findById(testAdvertisement.getId());
        assertEquals(4.5, advertisementService.ratingAvg(persistedAd.getId()));
    }

    @Test
    void testAddComment() {
        String comment = "Great apartment!";
        Advertisement commentedAd = advertisementService.addComment(comment, testAdvertisement.getId());

        assertTrue(commentedAd.getComments().contains(comment));

        Advertisement persistedAd = advertisementService.findById(testAdvertisement.getId());
        assertEquals(1, advertisementService.numbComments(persistedAd.getId()));
        assertTrue(persistedAd.getComments().contains(comment));
    }

    private void setupTestDataForFiltering() {
        List<Advertisement> ads = new ArrayList<>();
        ads.add(testAdvertisement);

        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("image.jpg");

        Apartment apartment2 = apartmentService.create(
                MunicipalityType.Aerodrom,
                "Second Address",
                2,
                60,
                imageUrls,
                "Small Apartment",
                testUser.getUsername()
        );
        Advertisement ad2 = advertisementService.create(
                apartment2.getId(),
                AdvertisementType.RENT,
                50000.0,
                testUser.getUsername()
        );
        ad2.setRatings(new HashMap<>());
        advertisementService.addRating(3.5, ad2.getId(), "testuser");
        ads.add(ad2);

        Apartment apartment3 = apartmentService.create(
                MunicipalityType.Center,
                "Third Address",
                3,
                80,
                imageUrls,
                "Medium Apartment",
                testUser.getUsername()
        );
        Advertisement ad3 = advertisementService.create(
                apartment3.getId(),
                AdvertisementType.SELL,
                75000.0,
                testUser.getUsername()
        );
        ad3.setRatings(new HashMap<>());
        advertisementService.addRating(4.5, ad3.getId(), "testuser");
        ads.add(ad3);

        Apartment apartment4 = apartmentService.create(
                MunicipalityType.Butel,
                "Fourth Address",
                4,
                120,
                imageUrls,
                "Large Apartment",
                testUser.getUsername()
        );
        Advertisement ad4 = advertisementService.create(
                apartment4.getId(),
                AdvertisementType.SELL,
                150000.0,
                testUser.getUsername()
        );
        ad4.setRatings(new HashMap<>());
        advertisementService.addRating(5.0, ad4.getId(), "testuser");
        ads.add(ad4);

    }

    @Test
    void testFilterByPrice() {
        setupTestDataForFiltering();

        // Test priceMore filter
        List<Advertisement> expensiveAds = advertisementService.filter(
                120000.0, null, null, null, null, null,
                null, null, null, null);
        assertEquals(1, expensiveAds.size());
        assertEquals(150000.0, expensiveAds.get(0).getPrice());

        // Test priceLess filter
        List<Advertisement> cheapAds = advertisementService.filter(
                null, 60000.0, null, null, null, null,
                null, null, null, null);
        assertEquals(1, cheapAds.size());
        assertEquals(50000.0, cheapAds.get(0).getPrice());

        // Test price range
        List<Advertisement> midRangeAds = advertisementService.filter(
                70000.0, 120000.0, null, null, null, null,
                null, null, null, null);
        assertEquals(2, midRangeAds.size());
    }

    @Test
    void testFilterByMunicipality() {
        setupTestDataForFiltering();

        List<Advertisement> centerAds = advertisementService.filter(
                null, null, MunicipalityType.Center, null, null, null,
                null, null, null, null);
        assertEquals(2, centerAds.size()); // Original ad and one from test data

        List<Advertisement> aerodromAds = advertisementService.filter(
                null, null, MunicipalityType.Aerodrom, null, null, null,
                null, null, null, null);
        assertEquals(1, aerodromAds.size());
    }

    @Test
    void testFilterByRating() {
        setupTestDataForFiltering();

        // Test avgRatingMore filter
        List<Advertisement> highRatedAds = advertisementService.filter(
                null, null, null, 4.5, null, null,
                null, null, null, null);
        assertEquals(2, highRatedAds.size());

        // Test avgRatingLess filter
        List<Advertisement> lowRatedAds = advertisementService.filter(
                null, null, null, null, 4.0, null,
                null, null, null, null);
        assertEquals(2, lowRatedAds.size());

        // Test rating range
        List<Advertisement> midRatedAds = advertisementService.filter(
                null, null, null, 4.0, 4.8, null,
                null, null, null, null);
        assertEquals(1, midRatedAds.size());
    }

    @Test
    void testFilterBySize() {
        setupTestDataForFiltering();

        // Test sizeMore filter
        List<Advertisement> largeAds = advertisementService.filter(
                null, null, null, null, null, null,
                null, 100, null, null);
        assertEquals(2, largeAds.size());

        // Test sizeLess filter
        List<Advertisement> smallAds = advertisementService.filter(
                null, null, null, null, null, null,
                null, null, 70, null);
        assertEquals(1, smallAds.size());

        // Test size range
        List<Advertisement> midSizeAds = advertisementService.filter(
                null, null, null, null, null, null,
                null, 70, 100, null);
        assertEquals(2, midSizeAds.size());
    }

    @Test
    void testFilterByRooms() {
        setupTestDataForFiltering();

        List<Advertisement> threeRoomAds = advertisementService.filter(
                null, null, null, null, null, null,
                3, null, null, null);
        assertEquals(2, threeRoomAds.size());

        List<Advertisement> fourRoomAds = advertisementService.filter(
                null, null, null, null, null, null,
                4, null, null, null);
        assertEquals(1, fourRoomAds.size());
    }

    @Test
    void testFilterByType() {
        setupTestDataForFiltering();

        List<Advertisement> sellAds = advertisementService.filter(
                null, null, null, null, null, null,
                null, null, null, AdvertisementType.SELL);
        assertEquals(3, sellAds.size());

        List<Advertisement> rentAds = advertisementService.filter(
                null, null, null, null, null, null,
                null, null, null, AdvertisementType.RENT);
        assertEquals(1, rentAds.size());
    }

    @Test
    void testComplexFilter() {
        setupTestDataForFiltering();

        // Test combination of multiple filters
        List<Advertisement> filteredAds = advertisementService.filter(
                70000.0,
                200000.0,
                MunicipalityType.Center,
                4.0,
                5.0,
                null,
                3,
                70,
                100,
                AdvertisementType.SELL
        );

        assertEquals(1, filteredAds.size());
        Advertisement ad = filteredAds.get(0);
        assertEquals(MunicipalityType.Center, ad.getApartment().getMunicipality());
        assertEquals(AdvertisementType.SELL, ad.getType());
        assertTrue(ad.getPrice() >= 70000.0 && ad.getPrice() <= 200000.0);
        assertTrue(ad.getApartment().getSize() >= 70 && ad.getApartment().getSize() <= 100);
        assertEquals(3, ad.getApartment().getNumRooms());
    }

    @Test
    void testMinMaxValues() {
        // Create additional advertisement with different values
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("image.jpg");

        Apartment apartment2 = apartmentService.create(
                MunicipalityType.Aerodrom,
                "Second Address",
                2,
                80,
                imageUrls,
                "Second Apartment",
                testUser.getUsername()
        );

        advertisementService.create(
                apartment2.getId(),
                AdvertisementType.RENT,
                50000.0,
                testUser.getUsername()
        );

        // Test min/max methods
        assertEquals(80, advertisementService.minSize());
        assertEquals(100, advertisementService.maxSize());
        assertEquals(50000.0, advertisementService.minPrice());
        assertEquals(100000.0, advertisementService.maxPrice());
    }

    @Test
    void testDelete() {
        Long adId = testAdvertisement.getId();

        Advertisement deletedAd = advertisementService.delete(adId);

        assertEquals(testAdvertisement.getId(), deletedAd.getId());
        assertTrue(advertisementRepository.findById(adId).isEmpty());
        assertEquals(0, advertisementService.listAll().size());
    }
}
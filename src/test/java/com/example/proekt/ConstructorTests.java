package com.example.proekt;

import com.example.proekt.model.*;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConstructorTests {
    @Test
    void adsTestConstructor() {
        Apartment apartment = new Apartment();
        User owner = new User();
        Advertisement ad = new Advertisement(apartment, AdvertisementType.SELL, 100000.0, owner);

        assertEquals(apartment, ad.getApartment());
        assertEquals(AdvertisementType.SELL, ad.getType());
        assertEquals(100000.0, ad.getPrice());
        assertEquals(owner, ad.getOwner());
    }

    @Test
    void testGetRatingAvg() {
        Advertisement ad = new Advertisement();
        Map<String, Double> ratings = new HashMap<>();
        ratings.put("user1", 4.0);
        ratings.put("user2", 5.0);
        ad.setRatings(ratings);

        assertEquals(4.5, ad.getRatingAvg());
    }

    @Test
    void apartmentsTestConstructor() {
        User owner = new User();
        List<String> imageUrls = Arrays.asList("url1", "url2");

        Apartment apartment = new Apartment(
                MunicipalityType.Center,
                "Test Address",
                3,
                100,
                imageUrls,
                "Test Title",
                owner
        );

        assertEquals(MunicipalityType.Center, apartment.getMunicipality());
        assertEquals("Test Address", apartment.getAddress());
        assertEquals(Integer.valueOf(3), apartment.getNumRooms());
        assertEquals(Integer.valueOf(100), apartment.getSize());
        assertEquals(imageUrls, apartment.getImageUrls());
        assertEquals("Test Title", apartment.getTitle());
        assertEquals(owner, apartment.getOwner());
    }
    @Test
    void messagesTestConstructor() {
        User sender = new User();
        User recipient = new User();
        LocalDateTime now = LocalDateTime.now();

        Message message = new Message(sender, recipient, "Test content", now);

        assertEquals(sender, message.getSender());
        assertEquals(recipient, message.getRecipient());
        assertEquals("Test content", message.getContent());
        assertEquals(now, message.getSentAt());
    }

    @Test
    void msgThreadTestConstructor() {
        User user1 = new User();
        User user2 = new User();
        List<Message> messages = new ArrayList<>();
        Advertisement ad = new Advertisement();

        MessageThread thread = new MessageThread(user1, user2, messages, ad);

        assertEquals(user1, thread.getUser1());
        assertEquals(user2, thread.getUser2());
        assertEquals(messages, thread.getMessages());
        assertEquals(ad, thread.getAdvertisement());
    }

    @Test
    void userTestConstructor() {
        User user = new User("testuser", "password", Role.ROLE_USER);

        assertEquals("testuser", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals(Role.ROLE_USER, user.getRole());
    }

    @Test
    void userTestUserDetails() {
        User user = new User("testuser", "password", Role.ROLE_USER);

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_USER.name())));
    }
}

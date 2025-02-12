package com.example.proekt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Apartment {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private MunicipalityType municipality;

    private String address;

    private Integer numRooms;

    private Integer size;

    @ManyToOne
    private User owner;

    @ElementCollection
    private List<String> imageUrls;

    public Apartment(MunicipalityType municipality, String address, Integer numRooms, Integer size, List<String> imageUrls, String title, User owner) {
        this.municipality = municipality;
        this.address = address;
        this.numRooms = numRooms;
        this.size = size;
        this.imageUrls = imageUrls;
        this.title=title;
        this.owner=owner;
    }

    public Apartment() {
    }
}

package com.example.proekt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
public class Advertisement{

    @Id
    @GeneratedValue
    private Long id;

    @ElementCollection
    private Map<String,Double> ratings;

    @ElementCollection
    private List<String> comments;

    @Enumerated(EnumType.STRING)
    private AdvertisementType type;

    private Double price;

    @ManyToOne
    private Apartment apartment;

    @ManyToOne
    private User owner;

    public Advertisement() {
        this.ratings = new HashMap<>();
    }

    public Advertisement(Apartment apartment, AdvertisementType type, Double price, User owner) {
        this.apartment = apartment;
        this.type=type;
        this.price=price;
        this.owner=owner;
    }

    public Double getRatingAvg() {
        Map<String,Double> ratings= this.getRatings();
        Double avg= ratings.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        return avg;
    }
}


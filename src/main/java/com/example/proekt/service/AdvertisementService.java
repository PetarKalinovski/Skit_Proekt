package com.example.proekt.service;

import com.example.proekt.model.Advertisement;
import com.example.proekt.model.AdvertisementType;
import com.example.proekt.model.MunicipalityType;
import com.example.proekt.model.User;

import java.util.List;


public interface AdvertisementService {

    List<Advertisement> listAll();

    Advertisement findById(Long id);

    Advertisement create(Long apartmentID, AdvertisementType type, Double price, String owner);

    Advertisement update(Long id,Long apartmentID, AdvertisementType type, Double price);

    Advertisement addRating(Double rating, Long id, String username);

    Double ratingAvg(Long id);

    Integer numbComments(Long id);
    Advertisement addComment(String comment, Long id);

    Advertisement delete(Long id);

    Integer maxSize();
    Integer minSize();

    Double maxPrice();
    Double minPrice();

    List<Advertisement> filter(Double priceMore, Double priceLess, MunicipalityType municipality, Double avgRatingMore,
                               Double avgRatingLess, Double comments, Integer numRooms, Integer sizeMore, Integer sizeLess, AdvertisementType advertisementType);

}

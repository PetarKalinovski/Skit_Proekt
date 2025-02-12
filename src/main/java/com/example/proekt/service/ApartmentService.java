package com.example.proekt.service;

import com.example.proekt.model.Apartment;
import com.example.proekt.model.MunicipalityType;
import com.example.proekt.model.User;

import java.util.List;

public interface ApartmentService {

    Apartment findById(Long id);

    List<Apartment> listAll();

    Apartment create(MunicipalityType municipality, String address, Integer numRooms, Integer size, List<String> imageUrls, String title, String user);

    Apartment update(Long id, MunicipalityType municipality, String address, Integer numRooms, Integer size, List<String> imageUrls, String title);

    Apartment delete(Long id);
}

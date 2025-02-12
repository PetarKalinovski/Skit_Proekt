package com.example.proekt.service.impl;

import com.example.proekt.model.Apartment;
import com.example.proekt.model.MunicipalityType;
import com.example.proekt.model.exceptions.InvalidAppartIdException;
import com.example.proekt.repository.ApartmentReopository;
import com.example.proekt.service.ApartmentService;
import com.example.proekt.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ApartmentServiceImpl implements ApartmentService {

    private final ApartmentReopository apartmentReopository;
    private final UserService userService;

    public ApartmentServiceImpl(ApartmentReopository apartmentReopository, UserService userService) {
        this.apartmentReopository = apartmentReopository;
        this.userService = userService;
    }

    @Override
    public Apartment findById(Long id) {
        return apartmentReopository.findById(id).orElseThrow(InvalidAppartIdException::new);
    }

    @Override
    public List<Apartment> listAll() {
        return apartmentReopository.findAll();
    }

    @Override
    public Apartment create(MunicipalityType municipality, String address, Integer numRooms, Integer size, List<String> imageUrls, String title, String user) {
        return apartmentReopository.save(new Apartment(municipality,address,numRooms,size,imageUrls,title,userService.findByUsername(user)));
    }

    @Override
    public Apartment update(Long id, MunicipalityType municipality, String address, Integer numRooms, Integer size, List<String> imageUrls, String title) {
        Apartment a=this.findById(id);
        a.setMunicipality(municipality);
        a.setAddress(address);
        a.setNumRooms(numRooms);
        a.setSize(size);
        a.setImageUrls(imageUrls);
        a.setTitle(title);
        return apartmentReopository.save(a);
    }

    @Override
    public Apartment delete(Long id) {
        Apartment a = this.findById(id);
        apartmentReopository.delete(a);
        return a;
    }
}

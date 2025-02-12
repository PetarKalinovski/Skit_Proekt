package com.example.proekt.repository;

import com.example.proekt.model.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApartmentReopository extends JpaRepository<Apartment,Long> {

}

package com.aaro.securemanagementsystem.repo;

import com.aaro.securemanagementsystem.models.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessRepo extends JpaRepository<Business, Integer> {
    Optional<Business> findByName(String name);
}

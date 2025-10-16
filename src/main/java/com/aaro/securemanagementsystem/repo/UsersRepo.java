package com.aaro.securemanagementsystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepo extends JpaRepository<OtherUserRepo, Integer> {

    Optional<OtherUserRepo> findByEmail(String email);

    List<OtherUserRepo> findByRole(String role);

    List<OtherUserRepo> findAllByBusiness_Id(Integer businessId);

    List<OtherUserRepo> findByRoleAndBusiness_Id(String role, Integer businessId);

    Optional<OtherUserRepo> findByIdAndBusiness_Id(Integer id, Integer businessId);
}
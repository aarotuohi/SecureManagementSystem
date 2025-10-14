package com.aaro.securemanagementsystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepo extends JpaRepository<OtherUserRepo, Integer> {

    Optional<OtherUserRepo> findByEmail(String email);

    List<OtherUserRepo> findByRole(String role);
}
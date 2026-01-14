package com.kiks.dishdashapi.repo;

import com.kiks.dishdashapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByFullName(String fullName);
    Optional<User> findByEmail(String email);
    List<User> findAllByDateOfBirth(LocalDate dateOfBirth);

    String email(String email);
}

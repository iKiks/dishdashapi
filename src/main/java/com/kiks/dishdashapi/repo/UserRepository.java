package com.kiks.dishdashapi.repo;

import com.kiks.dishdashapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByFullName(String fullName);
    User findByEmail(String email);

    User findAllByDateOfBirth(LocalDate dateOfBirth);
}

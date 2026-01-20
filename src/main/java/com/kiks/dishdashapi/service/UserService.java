package com.kiks.dishdashapi.service;

import com.kiks.dishdashapi.model.User;
import com.kiks.dishdashapi.repo.UserRepository;
import org.springframework.security.crypto.password4j.Argon2Password4jPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repo;

    private final Argon2Password4jPasswordEncoder encoder = new Argon2Password4jPasswordEncoder();

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User saveUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public boolean existByEmail(String email) {
        return repo.existsByEmail(email);
    }


}

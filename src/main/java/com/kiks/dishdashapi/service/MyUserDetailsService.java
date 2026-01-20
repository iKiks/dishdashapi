package com.kiks.dishdashapi.service;

import com.kiks.dishdashapi.model.User;
import com.kiks.dishdashapi.model.UserPrincipal;
import com.kiks.dishdashapi.repo.UserRepository;
import org.jspecify.annotations.NullUnmarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    public MyUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }


    @Override
    @NullUnmarked
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       User user = repo.findByEmail(email);

        if (user==null) {
            System.out.println("User 404");
            throw new UsernameNotFoundException("User 404");
        }
        return new UserPrincipal(user);
    }

}

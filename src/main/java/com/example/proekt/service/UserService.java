package com.example.proekt.service;

import com.example.proekt.model.Role;
import com.example.proekt.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
        User register(String username, String password, String repeatPassword, Role role);

        User findByUsernameAndPasword(String username, String password);

        User findByUsername(String username);

}

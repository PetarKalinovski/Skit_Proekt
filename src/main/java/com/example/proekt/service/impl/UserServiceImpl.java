package com.example.proekt.service.impl;

import com.example.proekt.model.Role;
import com.example.proekt.model.User;
import com.example.proekt.model.exceptions.InvalidUserCredentialsException;
import com.example.proekt.model.exceptions.InvalidUsernameOrPasswordException;
import com.example.proekt.model.exceptions.PasswordsDoNotMatchException;
import com.example.proekt.model.exceptions.UsernameAlreadyExistsException;
import com.example.proekt.repository.UserRepository;
import com.example.proekt.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User register(String username, String password, String repeatPassword, Role role) {
        if (username==null || username.isEmpty()  || password==null || password.isEmpty())
            throw new InvalidUsernameOrPasswordException();
        if (!password.equals(repeatPassword))
            throw new PasswordsDoNotMatchException();
        if(this.userRepository.findByUsername(username).isPresent())
            throw new UsernameAlreadyExistsException();
        User user = new User(username,passwordEncoder.encode(password),role);
        return userRepository.save(user);

    }


    @Override
    public User findByUsernameAndPasword(String username, String password) {
        return this.userRepository.findByUsernameAndPassword(username,password).orElseThrow(InvalidUserCredentialsException::new);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(InvalidUsernameOrPasswordException::new);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException(username));
    }
}

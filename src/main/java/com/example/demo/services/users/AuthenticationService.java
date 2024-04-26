package com.example.demo.services.users;

import com.example.demo.entities.users.Tokens;
import com.example.demo.entities.users.User;
import com.example.demo.repositories.users.TokenRepository;
import com.example.demo.repositories.users.UserRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    public String registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            // User with the same email already exists, return an error message or throw an exception
            throw new IllegalArgumentException("Email is already in use");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        // Generate JWT token for the registered user
        String jwtToken = userService.generateJwtToken(new org.springframework.security.core.userdetails.User(user.getUsername(), "", new ArrayList<>()));

        userService.saveToken(jwtToken, user);

        return jwtToken;
    }


    public String loginUser(String username, String password) {
        // Retrieve the user details from the database
        User user = userService.loadUserByUsername(username);

        // Check if the provided password matches the encoded password in the database
        if (passwordEncoder.matches(password, user.getPassword())) {
            // Passwords match, generate JWT token for the authenticated user
            String jwtToken = userService.generateJwtToken(user);

            // Save the generated token for the user
            userService.saveToken(jwtToken, user);

            return jwtToken;
        } else {
            // Passwords don't match, throw an exception or return an error message
            throw new IllegalArgumentException("Invalid credentials");
        }
    }

}
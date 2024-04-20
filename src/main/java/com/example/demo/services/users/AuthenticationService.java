package com.example.demo.services.users;

import com.example.demo.entities.users.User;
import com.example.demo.repositories.users.UserRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Date;

@Service
public class AuthenticationService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    public String generateJwtToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject((userDetails.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public UserDetails getUserDetailsFromJwtToken(String token) {
        String username = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return new org.springframework.security.core.userdetails.User(username, "", new ArrayList<>());
    }


    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            // Invalid JWT token
            // log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // JWT token is expired
            // log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // JWT token is unsupported
            // log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // JWT claims string is empty
            // log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), new ArrayList<>());
    }

    public User registerUser(User user) {
        // Validate and save user to database
        // You can also encode the password before saving
        return userRepository.save(user);
    }

    // JWT token generation, validation methods
}
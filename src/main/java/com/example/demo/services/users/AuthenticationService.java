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
public class AuthenticationService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String generateJwtToken(UserDetails userDetails) {
        String token = Jwts.builder()
                .setSubject((userDetails.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return token;
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

    public String registerUser(User user) {
        Optional<Object> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            // User with the same email already exists, return an error message or throw an exception
            throw new IllegalArgumentException("Email is already in use");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        // Generate JWT token for the registered user
        String jwtToken = this.generateJwtToken(new org.springframework.security.core.userdetails.User(user.getUsername(), "", new ArrayList<>()));

        this.saveToken(jwtToken, user);

        return jwtToken;
    }

    private void saveToken(String jwtToken, User user) {
        Tokens tokenEntity = new Tokens();
        tokenEntity.setToken(jwtToken);
        tokenEntity.setUser_id(user.getId());

        LocalDateTime expirationDateTime = LocalDateTime.now().plusHours(24);
        tokenEntity.setExpiration(Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant()));

        tokenRepository.save(tokenEntity);
    }

}
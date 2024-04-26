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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService  {
    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    @Autowired
    private TokenRepository tokenRepository;


    public String generateJwtToken(UserDetails userDetails) {
        String token = Jwts.builder()
                .setSubject((userDetails.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return token;
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
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    void saveToken(String jwtToken, User user) {
        Tokens tokenEntity = new Tokens();
        tokenEntity.setToken(jwtToken);
        tokenEntity.setUser_id(user.getId());

        tokenEntity.setExpiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24));

        tokenRepository.save(tokenEntity);
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }
}

package com.example.demo.repositories.users;

import com.example.demo.entities.users.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Tokens, Long> {

    Optional<Tokens> findByToken(String token);

}

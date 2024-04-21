package com.example.demo.repositories.users;

import com.example.demo.entities.users.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Tokens, Long> {

}

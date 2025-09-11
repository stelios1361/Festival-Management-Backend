package com.festivalmanager.repository;

import com.festivalmanager.model.Token;
import com.festivalmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for managing tokens.
 */
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUser(User user);
    Optional<Token> findByValue(String value);
    void deleteByUser(User user);
}

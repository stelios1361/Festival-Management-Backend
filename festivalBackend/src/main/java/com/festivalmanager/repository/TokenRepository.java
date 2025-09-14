package com.festivalmanager.repository;

import com.festivalmanager.model.Token;
import com.festivalmanager.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for managing tokens.
 */
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllByUser(User user);
    Optional<Token> findByValue(String value);
    void deleteByUser(User user);
}

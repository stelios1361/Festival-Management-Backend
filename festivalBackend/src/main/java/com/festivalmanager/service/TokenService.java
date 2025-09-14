package com.festivalmanager.service;

import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.Token;
import com.festivalmanager.model.User;
import com.festivalmanager.repository.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for managing authentication tokens.
 */
@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    /**
     * Creates a new token for the given user, deleting any old ones.
     *
     * @param user the user to generate a token for
     * @return the generated token
     */
    @Transactional
    public Token generateToken(User user) {
        // Invalidate existing tokens instead of deleting
        var oldTokens = tokenRepository.findAllByUser(user);
        oldTokens.forEach(t -> t.setActive(false));
        tokenRepository.saveAll(oldTokens);

        // Create new one
        Token token = new Token();
        token.setUser(user);
        token.setValue(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusHours(2));
        token.setActive(true);
        return tokenRepository.save(token);
    }

    /**
     * Validates a token string.
     *
     * @param value the token value
     * @param requestingUser the requesting user
     * @return the valid token
     */
    public boolean validateToken(String value, User requestingUser) {
        System.out.println("Given token: " + value + " Given username: " +requestingUser.getUsername());
        var token = tokenRepository.findByValue(value)
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));
        System.out.println("Username that the token belongs too: "+ token.getUser().getUsername());
        if (!token.isActive()) {
            throw new ApiException("Token is inactive", HttpStatus.UNAUTHORIZED);
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Token expired", HttpStatus.UNAUTHORIZED);
        }

        if (!token.getUser().equals(requestingUser)) {
            // both accounts must be deactivated!
            token.getUser().setActive(false);
            requestingUser.setActive(false);
            throw new ApiException("Token belongs to another user. Accounts deactivated.", HttpStatus.FORBIDDEN);
        }

        return true;
    }
}

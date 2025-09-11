package com.festivalmanager.service;

import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.Token;
import com.festivalmanager.model.User;
import com.festivalmanager.repository.TokenRepository;
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
    public Token generateToken(User user) {
        tokenRepository.deleteByUser(user);

        Token token = new Token();
        token.setValue(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusHours(2)); // valid 2h
        token.setUser(user);

        return tokenRepository.save(token);
    }

    /**
     * Validates a token string.
     *
     * @param tokenValue the token value
     * @param user the requesting user (optional check)
     * @return the valid token
     */
    public Token validateToken(String tokenValue, User user) {
        Token token = tokenRepository.findByValue(tokenValue)
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Token expired", HttpStatus.UNAUTHORIZED);
        }

        if (user != null && !token.getUser().getId().equals(user.getId())) {
            // deactivate both accounts as per spec
            token.getUser().setActive(false);
            user.setActive(false);
            throw new ApiException("Token does not belong to user. Accounts deactivated.", HttpStatus.FORBIDDEN);
        }

        return token;
    }

    /**
     * Cancels a user’s token (delete it).
     *
     * @param user the user
     */
    public void cancelToken(User user) {
        tokenRepository.deleteByUser(user);
    }
}

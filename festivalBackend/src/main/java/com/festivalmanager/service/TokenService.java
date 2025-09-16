package com.festivalmanager.service;

import com.festivalmanager.exception.ApiException;
import com.festivalmanager.enums.PermanentRoleType;
import com.festivalmanager.model.Token;
import com.festivalmanager.model.User;
import com.festivalmanager.repository.TokenRepository;
import com.festivalmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

/**
 * Service for managing authentication tokens. Handles token generation,
 * validation, deactivation, and deletion.
 */
@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;

    // -------------------- TOKEN GENERATION --------------------
    /**
     * Generates a new token for the given user. Invalidates all existing
     * tokens.
     *
     * @param user the user for whom the token is generated
     * @return the newly created Token
     */
    @Transactional
    public Token generateToken(User user) {
        // Invalidate existing tokens
        List<Token> oldTokens = tokenRepository.findAllByUser(user);
        oldTokens.forEach(t -> t.setActive(false));
        tokenRepository.saveAll(oldTokens);

        // Create new token
        Token token = new Token();
        token.setUser(user);
        token.setValue(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusHours(2));
        token.setActive(true);

        return tokenRepository.save(token);
    }

    // -------------------- TOKEN DEACTIVATION --------------------
    /**
     * Deactivates all tokens of a given user.
     *
     * @param user the user whose tokens should be deactivated
     */
    @Transactional
    public void deactivateTokens(User user) {
        List<Token> tokens = tokenRepository.findAllByUser(user);
        tokens.forEach(t -> t.setActive(false));
        tokenRepository.saveAll(tokens);
    }

    // -------------------- TOKEN DELETION --------------------
    /**
     * Deletes all tokens associated with a given user.
     *
     * @param user the user whose tokens should be deleted
     */
    @Transactional
    public void deleteTokens(User user) {
        tokenRepository.deleteByUser(user);
    }

    // -------------------- TOKEN VALIDATION --------------------
    /**
     * Validates a token string for a given user.
     *
     * @param value the token value
     * @param requestingUser the user making the request
     * @return true if the token is valid
     * @throws ApiException if token is invalid, expired, inactive, or belongs
     * to another user
     */
    public boolean validateToken(String value, User requestingUser) {
        Token token = tokenRepository.findByValue(value)
                .orElseThrow(() -> new ApiException("Invalid token", HttpStatus.UNAUTHORIZED));

        if (!token.isActive()) {
            throw new ApiException("Token is inactive", HttpStatus.UNAUTHORIZED);
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException("Token expired", HttpStatus.UNAUTHORIZED);
        }

        if (!token.getUser().equals(requestingUser)) {
            // Protect admins: never deactivate them
            if (token.getUser().getPermanentRole() != PermanentRoleType.ADMIN) {
                token.getUser().setActive(false);
                userRepository.saveAndFlush(token.getUser());
            }
            if (requestingUser.getPermanentRole() != PermanentRoleType.ADMIN) {
                requestingUser.setActive(false);
                userRepository.saveAndFlush(requestingUser);
            }

            throw new ApiException(
                    "Token belongs to another user. Accounts deactivated (except ADMIN).",
                    HttpStatus.FORBIDDEN
            );
        }

        return true;
    }
    
    
}

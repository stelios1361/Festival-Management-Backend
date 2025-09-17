package com.festivalmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.Token;
import com.festivalmanager.model.User;
import com.festivalmanager.repository.TokenRepository;
import com.festivalmanager.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Manual injection of mocks
        tokenService = new TokenService();
        tokenService.tokenRepository = tokenRepository;
        tokenService.userRepository = userRepository;

        System.out.println("=== TokenServiceTest setup completed ===\n");
    }

    @Test
    void testGenerateToken() {
        User user = new User();
        Token oldToken = new Token();
        oldToken.setActive(true);

        when(tokenRepository.findAllByUser(user)).thenReturn(Collections.singletonList(oldToken));
        when(tokenRepository.saveAll(anyList())).thenReturn(Collections.singletonList(oldToken));
        when(tokenRepository.save(any(Token.class))).thenAnswer(i -> i.getArgument(0));

        System.out.println("Running testGenerateToken");

        Token newToken = tokenService.generateToken(user);

        assertNotNull(newToken.getValue(), "Generated token value should not be null");
        assertTrue(newToken.isActive(), "Generated token should be active");
        assertEquals(user, newToken.getUser(), "Generated token should belong to the correct user");

        verify(tokenRepository).saveAll(anyList());
        verify(tokenRepository).save(newToken);

        System.out.println("testGenerateToken completed successfully\n");
    }

    @Test
    void testDeactivateTokens() {
        User user = new User();
        Token token = new Token();
        token.setActive(true);

        when(tokenRepository.findAllByUser(user)).thenReturn(Collections.singletonList(token));
        when(tokenRepository.saveAll(anyList())).thenReturn(Collections.singletonList(token));

        System.out.println("Running testDeactivateTokens");

        tokenService.deactivateTokens(user);

        assertFalse(token.isActive(), "Token should be deactivated");
        verify(tokenRepository).saveAll(anyList());

        System.out.println("testDeactivateTokens completed successfully\n");
    }

    @Test
    void testDeleteTokens() {
        User user = new User();

        System.out.println("Running testDeleteTokens");

        tokenService.deleteTokens(user);

        verify(tokenRepository).deleteByUser(user);

        System.out.println("testDeleteTokens completed successfully\n");
    }

    @Test
    void testValidateToken_success() {
        User user = new User();
        user.setUsername("alice");
        user.setActive(true);

        Token token = new Token();
        token.setValue("token123");
        token.setUser(user);
        token.setActive(true);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findByValue("token123")).thenReturn(Optional.of(token));

        System.out.println("Running testValidateToken_success");

        boolean result = tokenService.validateToken("token123", user);

        assertTrue(result, "Token should be valid");

        System.out.println("testValidateToken_success completed successfully\n");
    }

    @Test
    void testValidateToken_expired() {
        User user = new User();
        Token token = new Token();
        token.setValue("expiredToken");
        token.setUser(user);
        token.setActive(true);
        token.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findByValue("expiredToken")).thenReturn(Optional.of(token));
        when(tokenRepository.saveAndFlush(token)).thenReturn(token);

        System.out.println("Running testValidateToken_expired");

        ApiException ex = assertThrows(ApiException.class, ()
                -> tokenService.validateToken("expiredToken", user)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus(), "Expired token should throw UNAUTHORIZED");
        assertFalse(token.isActive(), "Expired token should be deactivated");

        System.out.println("testValidateToken_expired completed successfully\n");
    }
}

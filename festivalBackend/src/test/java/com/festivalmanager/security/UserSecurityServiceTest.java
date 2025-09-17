package com.festivalmanager.security;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.User;
import com.festivalmanager.repository.UserRepository;
import com.festivalmanager.service.TokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;

class UserSecurityServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    private UserSecurityService securityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Manual injection of mocks
        securityService = new UserSecurityService(userRepository, tokenService);

        System.out.println("=== UserSecurityServiceTest setup completed ===\n");
    }

    @Test
    void testValidateRequester_success() {
        User user = new User();
        user.setUsername("alice");
        user.setActive(true);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(tokenService.validateToken("token123", user)).thenReturn(true);

        System.out.println("Running testValidateRequester_success");

        User result = securityService.validateRequester("alice", "token123");

        assertEquals("alice", result.getUsername(), "Validated user should match requested username");
        verify(tokenService).validateToken("token123", user);

        System.out.println("testValidateRequester_success completed successfully\n");
    }

    @Test
    void testValidateRequester_userNotFound() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());

        System.out.println("Running testValidateRequester_userNotFound");

        ApiException ex = assertThrows(ApiException.class, () ->
                securityService.validateRequester("bob", "token123")
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus(), "Nonexistent user should throw UNAUTHORIZED");

        System.out.println("testValidateRequester_userNotFound completed successfully\n");
    }

    @Test
    void testValidateRequester_userInactive() {
        User user = new User();
        user.setUsername("charlie");
        user.setActive(false);

        when(userRepository.findByUsername("charlie")).thenReturn(Optional.of(user));

        System.out.println("Running testValidateRequester_userInactive");

        ApiException ex = assertThrows(ApiException.class, () ->
                securityService.validateRequester("charlie", "token123")
        );

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus(), "Inactive user should throw FORBIDDEN");

        System.out.println("testValidateRequester_userInactive completed successfully\n");
    }

    @Test
    void testValidateRequester_tokenInvalid() {
        User user = new User();
        user.setUsername("dave");
        user.setActive(true);

        when(userRepository.findByUsername("dave")).thenReturn(Optional.of(user));
        doThrow(new ApiException("Invalid token", HttpStatus.UNAUTHORIZED))
                .when(tokenService).validateToken("badToken", user);

        System.out.println("Running testValidateRequester_tokenInvalid");

        ApiException ex = assertThrows(ApiException.class, () ->
                securityService.validateRequester("dave", "badToken")
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus(), "Invalid token should throw UNAUTHORIZED");

        System.out.println("testValidateRequester_tokenInvalid completed successfully\n");
    }
}

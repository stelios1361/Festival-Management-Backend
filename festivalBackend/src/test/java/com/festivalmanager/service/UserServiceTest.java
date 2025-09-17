package com.festivalmanager.service;

import com.festivalmanager.dto.user.*;
import com.festivalmanager.enums.PermanentRoleType;
import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.User;
import com.festivalmanager.repository.UserRepository;
import com.festivalmanager.security.PasswordService;
import com.festivalmanager.security.UserSecurityService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserSecurityService userSecurityService;
    @Mock
    private PasswordService passwordService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, tokenService, userSecurityService, passwordService);
        System.out.println("=== UserServiceTest setup completed ===\n");
    }

    @Test
    void testRegisterUser_success() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("alice1");
        req.setPassword1("Strong@123");
        req.setPassword2("Strong@123");
        req.setFullname("Alice Example");

        when(userRepository.existsByUsername("alice1")).thenReturn(false);
        when(userRepository.count()).thenReturn(0L); // first user → admin
        when(passwordService.hash("Strong@123")).thenReturn("hashed");

        System.out.println("Running testRegisterUser_success");

        var response = userService.registerUser(req);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(userRepository).save(any(User.class));

        System.out.println("testRegisterUser_success completed successfully\n");
    }

    @Test
    void testRegisterUser_usernameAlreadyExists() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("bob");

        when(userRepository.existsByUsername("bob")).thenReturn(true);

        System.out.println("Running testRegisterUser_usernameAlreadyExists");

        ApiException ex = assertThrows(ApiException.class, () -> userService.registerUser(req));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());

        System.out.println("testRegisterUser_usernameAlreadyExists completed successfully\n");
    }

    @Test
    void testLoginUser_success() {
        User user = new User();
        user.setUsername("carol");
        user.setPassword("hashedPw");
        user.setActive(true);

        LoginRequest req = new LoginRequest();
        req.setUsername("carol");
        req.setPassword("pw123");

        when(userRepository.findByUsername("carol")).thenReturn(Optional.of(user));
        when(passwordService.matches("pw123", "hashedPw")).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn(new com.festivalmanager.model.Token());

        System.out.println("Running testLoginUser_success");

        var response = userService.loginUser(req);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        verify(tokenService).generateToken(user);

        System.out.println("testLoginUser_success completed successfully\n");
    }

    @Test
    void testLoginUser_deactivateAfter3FailedAttempts() {
        User user = new User();
        user.setUsername("dave");
        user.setPassword("hashedPw");
        user.setActive(true);
        user.setFailedLoginAttempts(2);

        LoginRequest req = new LoginRequest();
        req.setUsername("dave");
        req.setPassword("wrong");

        when(userRepository.findByUsername("dave")).thenReturn(Optional.of(user));
        when(passwordService.matches("wrong", "hashedPw")).thenReturn(false);

        System.out.println("Running testLoginUser_deactivateAfter3FailedAttempts");

        ApiException ex = assertThrows(ApiException.class, () -> userService.loginUser(req));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        assertFalse(user.isActive(), "User should be deactivated after 3 failed attempts");

        System.out.println("testLoginUser_deactivateAfter3FailedAttempts completed successfully\n");
    }

    @Test
    void testUpdateUserPassword_success() {
        User user = new User();
        user.setUsername("emma");
        user.setPassword("hashedOld");
        user.setActive(true);

        UpdatePasswordRequest req = new UpdatePasswordRequest();
        req.setRequesterUsername("emma");
        req.setToken("tkn");
        req.setOldPassword("old");
        req.setNewPassword1("New@1234");
        req.setNewPassword2("New@1234");

        when(userSecurityService.validateRequester("emma", "tkn")).thenReturn(user);
        when(passwordService.matches("old", "hashedOld")).thenReturn(true);
        when(passwordService.hash("New@1234")).thenReturn("hashedNew");
        when(tokenService.generateToken(user)).thenReturn(new com.festivalmanager.model.Token());

        System.out.println("Running testUpdateUserPassword_success");

        var response = userService.updateUserPassword(req);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("hashedNew", user.getPassword());

        System.out.println("testUpdateUserPassword_success completed successfully\n");
    }

    @Test
    void testDeleteUser_selfDelete() {
        User user = new User();
        user.setUsername("frank");

        DeleteUserRequest req = new DeleteUserRequest();
        req.setRequesterUsername("frank");
        req.setToken("tkn");

        when(userSecurityService.validateRequester("frank", "tkn")).thenReturn(user);

        System.out.println("Running testDeleteUser_selfDelete");

        userService.deleteUser(req);

        verify(tokenService).deleteTokens(user);
        verify(userRepository).delete(user);

        System.out.println("testDeleteUser_selfDelete completed successfully\n");
    }

    @Test
    void testRegisterUser_invalidUsernamePattern() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("123bad"); // invalid: starts with digit
        req.setPassword1("Strong@123");
        req.setPassword2("Strong@123");
        req.setFullname("Invalid Name");

        when(userRepository.existsByUsername("123bad")).thenReturn(false);
        when(userRepository.count()).thenReturn(1L);

        System.out.println("Running testRegisterUser_invalidUsernamePattern");

        ApiException ex = assertThrows(ApiException.class, () -> userService.registerUser(req));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("Invalid username"));

        System.out.println("testRegisterUser_invalidUsernamePattern completed successfully\n");
    }

    @Test
    void testRegisterUser_invalidPasswordPattern() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("validName");
        req.setPassword1("weak");   // invalid: too short, no uppercase/special
        req.setPassword2("weak");
        req.setFullname("Weak Password User");

        when(userRepository.existsByUsername("validName")).thenReturn(false);
        when(userRepository.count()).thenReturn(1L);

        System.out.println("Running testRegisterUser_invalidPasswordPattern");

        ApiException ex = assertThrows(ApiException.class, () -> userService.registerUser(req));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getMessage().contains("Password must be at least 8 characters"));

        System.out.println("testRegisterUser_invalidPasswordPattern completed successfully\n");
    }

}

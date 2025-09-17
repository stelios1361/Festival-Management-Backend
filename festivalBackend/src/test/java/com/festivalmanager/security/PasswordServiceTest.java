package com.festivalmanager.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService();
        System.out.println("=== PasswordServiceTest setup completed ===");
    }

    @Test
    void testHashAndMatch_success() {
        String rawPassword = "mySecret123";
        String hashedPassword = passwordService.hash(rawPassword);

        System.out.println("Running testHashAndMatch_success:");
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Hashed password: " + hashedPassword);

        assertNotNull(hashedPassword, "Hashed password should not be null");
        assertTrue(passwordService.matches(rawPassword, hashedPassword),
                "PasswordService should match raw password with hashed password");

        System.out.println("testHashAndMatch_success completed successfully\n");
    }

    @Test
    void testMatchFails_forWrongPassword() {
        String rawPassword = "password1";
        String hashedPassword = passwordService.hash("password2");

        System.out.println("Running testMatchFails_forWrongPassword:");
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Hashed password: " + hashedPassword);

        assertFalse(passwordService.matches(rawPassword, hashedPassword),
                "PasswordService should return false for non-matching passwords");

        System.out.println("testMatchFails_forWrongPassword completed successfully\n");
    }
}

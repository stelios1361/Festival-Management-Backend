package com.festivalmanager.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for handling password operations, such as hashing and
 * verifying passwords using BCrypt algorithm.
 * 
 * <p>This service provides methods to hash a plain text password and
 * to verify if a given plain text password matches a previously hashed
 * password.</p>
 */
@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Hashes a raw (plain text) password using the BCrypt hashing algorithm.
     *
     * @param rawPassword the plain text password to hash
     * @return a BCrypt-hashed version of the password
     */
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Checks if a raw (plain text) password matches a previously hashed password.
     *
     * @param rawPassword    the plain text password to verify
     * @param hashedPassword the hashed password to compare against
     * @return true if the raw password matches the hashed password, false otherwise
     */
    public boolean matches(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}

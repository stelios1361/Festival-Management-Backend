package com.festivalmanager.service;

import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.User;
import com.festivalmanager.model.PermanentRole;
import com.festivalmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {

        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ApiException("Username already exists!", HttpStatus.CONFLICT);
        }

        // Username validation
        if (!user.getUsername().matches("^[A-Za-z][A-Za-z0-9_]{4,}$")) {
            throw new ApiException(
                "Invalid username. Must be bigger than 5 characters, start with a letter, and contain only letters, digits, or _",
                HttpStatus.BAD_REQUEST
            );
        }

        // Password validation
        String password = user.getPassword();
        if (password.length() < 8 ||
            !password.matches(".*[A-Z].*") ||
            !password.matches(".*[a-z].*") ||
            !password.matches(".*[0-9].*") ||
            !password.matches(".*[^A-Za-z0-9].*")) {
            throw new ApiException(
                "Password must be bigger than 8 characters and contain uppercase, lowercase, number, and special character.",
                HttpStatus.BAD_REQUEST
            );
        }

        // Assign role (first user = ADMIN, rest = USER)
        if (userRepository.count() == 0) {
            user.setPermanentRole(PermanentRole.ADMIN);
            user.setActive(true);
        } else {
            user.setPermanentRole(PermanentRole.USER);
            user.setActive(false);
        }

        user.setFailedLoginAttempts(0);
        user.setFailedPasswordUpdates(0);

        return userRepository.save(user);
    }
}

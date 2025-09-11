package com.festivalmanager.service;

import com.festivalmanager.dto.ApiResponse;
import com.festivalmanager.dto.LoginRequest;
import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.User;
import com.festivalmanager.model.PermanentRole;
import com.festivalmanager.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;

    /**
     * Registers a new user in the system.
     * <p>
     * Validates username and password according to rules:
     * <ul>
     * <li>Username: at least 5 chars, starts with a letter, letters/digits/_
     * only</li>
     * <li>Password: at least 8 chars, contains uppercase, lowercase, number,
     * special char</li>
     * </ul>
     * <br>
     * Assigns the first user as ADMIN and others as USER.
     *
     * @param user the user to register
     * @return the saved {@link User} object
     * @throws ApiException if the username already exists or validation fails
     */
    //user registration 
    public ApiResponse<User> registerUser(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ApiException("Username already exists!", HttpStatus.CONFLICT);
        }

        if (!user.getUsername().matches("^[A-Za-z][A-Za-z0-9_]{4,}$")) {
            throw new ApiException(
                    "Invalid username. Must be bigger than 5 characters, start with a letter, and contain only letters, digits, or _",
                    HttpStatus.BAD_REQUEST
            );
        }

        String password = user.getPassword();
        if (password.length() < 8
                || !password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")
                || !password.matches(".*[0-9].*")
                || !password.matches(".*[^A-Za-z0-9].*")) {
            throw new ApiException(
                    "Password must be bigger than 8 characters and contain uppercase, lowercase, number, and special character.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (userRepository.count() == 0) {
            user.setPermanentRole(PermanentRole.ADMIN);
            user.setActive(true);
        } else {
            user.setPermanentRole(PermanentRole.USER);
            user.setActive(false);
        }

        user.setFailedLoginAttempts(0);
        user.setFailedPasswordUpdates(0);

        User savedUser = userRepository.save(user);

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "User registered successfully",
                savedUser
        );
    }

    public ApiResponse<Map<String, Object>> loginUser(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException("Invalid username or password", HttpStatus.UNAUTHORIZED));

        if (!user.isActive()) {
            throw new ApiException("Account is deactivated. Contact admin.", HttpStatus.FORBIDDEN);
        }

      
        if (!user.getPassword().equals(request.getPassword())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            if (user.getFailedLoginAttempts() >= 3) {
                user.setActive(false);
                userRepository.save(user);
                throw new ApiException("Account deactivated after 3 failed login attempts", HttpStatus.FORBIDDEN);
            }

            userRepository.save(user);
            throw new ApiException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        var token = tokenService.generateToken(user);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token.getValue());
        data.put("expiresAt", token.getExpiresAt());

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Login successful",
                data
        );
    }

}

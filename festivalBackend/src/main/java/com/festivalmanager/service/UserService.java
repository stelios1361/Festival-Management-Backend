package com.festivalmanager.service;

import com.festivalmanager.dto.api.ApiResponse;
import com.festivalmanager.dto.user.*;
import com.festivalmanager.exception.ApiException;
import com.festivalmanager.enums.PermanentRoleType;
import com.festivalmanager.model.User;
import com.festivalmanager.repository.UserRepository;
import com.festivalmanager.security.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 * Service class for handling all user-related operations: registration, login,
 * logout, information updates, password updates, account status changes and
 * deletion.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final UserSecurityService userSecurityService;
    private final PasswordService passwordService;

    // -------------------- USER REGISTRATION --------------------
    /**
     * Registers a new user.
     * <p>
     * Validates username and password. First user becomes ADMIN, others are
     * inactive.
     *
     * @param request the registration request containing user details
     * @return ApiResponse with success message
     * @throws ApiException if validation fails or username exists
     */
    @Transactional
    public ApiResponse<Map<String, Object>> registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            
            throw new ApiException("Username already exists!", HttpStatus.CONFLICT);
        }

        if (!request.getUsername().matches("^[A-Za-z][A-Za-z0-9_]{4,}$")) {
            throw new ApiException(
                    "Invalid username. Must start with a letter, be at least 5 characters, and contain only letters, digits, or _",
                    HttpStatus.BAD_REQUEST
            );
        }

        String pw1 = request.getPassword1();
        String pw2 = request.getPassword2();
        if (!pw1.equals(pw2)) {
            throw new ApiException("The two passwords must match!", HttpStatus.BAD_REQUEST);
        } else if (!isPasswordValid(pw1)) {
            throw new ApiException(
                    "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character.",
                    HttpStatus.BAD_REQUEST
            );
        }

        User user = new User();
        user.setFullName(request.getFullname());
        user.setUsername(request.getUsername());
        user.setPassword(passwordService.hash(pw1));
        user.setFailedLoginAttempts(0);
        user.setFailedPasswordUpdates(0);

        if (userRepository.count() == 0) {
            user.setPermanentRole(PermanentRoleType.ADMIN);
            user.setActive(true);
        } else {
            user.setPermanentRole(PermanentRoleType.USER);
            user.setActive(false);
        }

        userRepository.save(user);

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "User registered successfully",
                new HashMap<>()
        );
    }

    // -------------------- USER LOGIN --------------------
    /**
     * Authenticates a user and generates a token.
     *
     * @param request login request containing username and password
     * @return ApiResponse containing token and expiration
     * @throws ApiException if authentication fails or account is inactive
     */
    public ApiResponse<Map<String, Object>> loginUser(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException("Invalid username", HttpStatus.UNAUTHORIZED));

        if (!user.isActive()) {
            throw new ApiException("Account is deactivated. Please contact admin.", HttpStatus.FORBIDDEN);
        }

        if (!passwordService.matches(request.getPassword(), user.getPassword())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= 3) {
                user.setActive(false);
                userRepository.save(user);
                throw new ApiException("Account deactivated after 3 failed login attempts", HttpStatus.FORBIDDEN);
            }
            userRepository.save(user);
            throw new ApiException("Invalid password", HttpStatus.UNAUTHORIZED);
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

    // -------------------- UPDATE USER INFORMATION --------------------
    /**
     * Updates user information (full name or username). Admins can update other
     * users; self-update regenerates token.
     *
     * @param request update info request containing new details
     * @return ApiResponse with updated token if username changed
     */
    @Transactional
    public ApiResponse<Map<String, Object>> updateUserInfo(UpdateInfoRequest request) {
        Map<String, Object> data = new HashMap<>();

        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        User targetUser = (request.getTargetUsername() == null) ? requester : findTargetUser(requester, request.getTargetUsername());

        if (request.getNewFullName() != null) {
            targetUser.setFullName(request.getNewFullName());
        }

        if (request.getNewUsername() != null && !request.getNewUsername().equals(targetUser.getUsername())) {
            if (userRepository.existsByUsername(request.getNewUsername())) {
                throw new ApiException("Username already taken", HttpStatus.CONFLICT);
            }
            targetUser.setUsername(request.getNewUsername());

            var newToken = tokenService.generateToken(targetUser);
            data.put("token", newToken.getValue());
            data.put("expiresAt", newToken.getExpiresAt());
        }

        userRepository.save(targetUser);

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "User information updated successfully",
                data
        );
    }

    // -------------------- UPDATE USER PASSWORD --------------------
    /**
     * Updates user password. Invalidates old token and regenerates new token.
     * Deactivates account after 3 consecutive failed attempts.
     *
     * @param request update password request
     * @return ApiResponse with new token
     */
    public ApiResponse<Map<String, Object>> updateUserPassword(UpdatePasswordRequest request) {
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        Map<String, Object> data = new HashMap<>();
        var newToken = tokenService.generateToken(requester);
        data.put("token", newToken.getValue());
        data.put("expiresAt", newToken.getExpiresAt());

        if (!passwordService.matches(request.getOldPassword(), requester.getPassword())) {
            requester.setFailedPasswordUpdates(requester.getFailedPasswordUpdates() + 1);
            if (requester.getFailedPasswordUpdates() >= 3) {
                requester.setActive(false);
                userRepository.save(requester);
                throw new ApiException("Account deactivated after 3 failed password update attempts", HttpStatus.FORBIDDEN);
            }
            userRepository.save(requester);
            throw new ApiException("Old password is incorrect", HttpStatus.BAD_REQUEST);
        }

        if (!request.getNewPassword1().equals(request.getNewPassword2()) || !isPasswordValid(request.getNewPassword1())) {
            throw new ApiException("New password invalid or passwords do not match", HttpStatus.BAD_REQUEST);
        }

        requester.setPassword(passwordService.hash(request.getNewPassword1()));
        requester.setFailedPasswordUpdates(0);
        userRepository.save(requester);

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "Password updated successfully",
                data
        );
    }

    // -------------------- ACCOUNT STATUS UPDATE --------------------
    /**
     * Allows admin to activate/deactivate user accounts. Deactivates tokens if
     * user is deactivated.
     *
     * @param request account status request
     * @return ApiResponse
     */
    @Transactional
    public ApiResponse<Map<String, Object>> updateAccountStatus(UpdateAccountStatusRequest request) {
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        requireAdmin(requester);

        User targetUser = userRepository.findByUsername(request.getTargetUsername())
                .orElseThrow(() -> new ApiException("Target user not found", HttpStatus.NOT_FOUND));

        targetUser.setActive(request.getNewActive());
        userRepository.save(targetUser);

        if (!targetUser.isActive()) {
            tokenService.deactivateTokens(targetUser);
        }

        return new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                "User account status updated successfully", new HashMap<>());
    }

    // -------------------- DELETE USER --------------------
    /**
     * Deletes a user account. Admins can delete other users; a user can delete
     * their own account. Deletes all tokens for the deleted user.
     *
     * @param request delete user request
     * @return ApiResponse
     */
    @Transactional
    public ApiResponse<Map<String, Object>> deleteUser(DeleteUserRequest request) {
        //Validate requester
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        User targetUser;

        if (request.getTargetUsername() == null || request.getTargetUsername().isEmpty()) {
            // Self-deletion
            targetUser = requester;
        } else {
            // Admin deletion
            requireAdmin(requester); // throws exception if requester is not admin

            targetUser = userRepository.findByUsername(request.getTargetUsername())
                    .orElseThrow(() -> new ApiException("Target user not found", HttpStatus.NOT_FOUND));

            if (targetUser.equals(requester)) {
                // Prevent unnecessary self-deletion through targetUsername
                targetUser = requester;
            }
        }

        //Delete tokens
        tokenService.deleteTokens(targetUser);

        //Delete user
        userRepository.delete(targetUser);

        //Build response
        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "User deleted successfully",
                new HashMap<>()
        );
    }

    // -------------------- LOGOUT USER --------------------
    /**
     * Logs out a user. (Invalidates current token)
     *
     * @param request log out user request
     * @return ApiResponse
     */
    public ApiResponse<Map<String, Object>> logOutUser(LogoutRequest request) {
        User requester = userSecurityService.validateRequester(
                request.getRequesterUsername(),
                request.getToken()
        );

        tokenService.deactivateTokens(requester);

        return new ApiResponse<>(LocalDateTime.now(), HttpStatus.OK.value(),
                "User logged out successfully", new HashMap<>());
    }

    // -------------------- HELPERS --------------------
    private void requireAdmin(User requester) {
        if (requester.getPermanentRole() != PermanentRoleType.ADMIN) {
            throw new ApiException("Not authorized. Admin rights required.", HttpStatus.FORBIDDEN);
        }
    }

    private User findTargetUser(User requester, String targetUsername) {
        requireAdmin(requester);
        return userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new ApiException("Target user not found", HttpStatus.NOT_FOUND));
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^A-Za-z0-9].*");
    }
}

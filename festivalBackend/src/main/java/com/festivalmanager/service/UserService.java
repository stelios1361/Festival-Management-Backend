package com.festivalmanager.service;

import com.festivalmanager.dto.*;
import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.PermanentRole;
import com.festivalmanager.model.User;
import com.festivalmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for handling all user-related operations: registration, login,
 * logout ,information updates, password updates, account status changes and
 * deletion.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

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
        } else if (pw1.length() < 8
                || !pw1.matches(".*[A-Z].*")
                || !pw1.matches(".*[a-z].*")
                || !pw1.matches(".*[0-9].*")
                || !pw1.matches(".*[^A-Za-z0-9].*")) {
            throw new ApiException(
                    "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character.",
                    HttpStatus.BAD_REQUEST
            );
        }

        User user = new User();
        user.setFullName(request.getFullname());
        user.setUsername(request.getUsername());
        user.setPassword(pw1);
        user.setFailedLoginAttempts(0);
        user.setFailedPasswordUpdates(0);

        if (userRepository.count() == 0) {
            user.setPermanentRole(PermanentRole.ADMIN);
            user.setActive(true);
        } else {
            user.setPermanentRole(PermanentRole.USER);
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
                .orElseThrow(() -> new ApiException("Invalid username or password", HttpStatus.UNAUTHORIZED));

        if (!user.isActive()) {
            throw new ApiException("Account is deactivated. Please contact admin.", HttpStatus.FORBIDDEN);
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

    // -------------------- UPDATE USER INFORMATION --------------------
    /**
     * Updates user information (full name or username). Admins can update other
     * users; self-update regenerates token.
     *
     * @param request update info request containing new details
     * @return ApiResponse with updated token if username changed
     */
    public ApiResponse<Map<String, Object>> updateUserInfo(UpdateInfoRequest request) {
        Map<String, Object> data = new HashMap<>();

        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.UNAUTHORIZED));
        tokenService.validateToken(request.getToken(), requester);

        User targetUser;
        if (request.getTargetUsername() == null) {
            targetUser = requester;
        } else {
            if (requester.getPermanentRole() != PermanentRole.ADMIN) {
                throw new ApiException("Not authorized to update other users", HttpStatus.FORBIDDEN);
            }
            targetUser = userRepository.findByUsername(request.getTargetUsername())
                    .orElseThrow(() -> new ApiException("Target user not found", HttpStatus.NOT_FOUND));
        }

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
        User user = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.UNAUTHORIZED));
        tokenService.validateToken(request.getToken(), user);

        Map<String, Object> data = new HashMap<>();
        var newToken = tokenService.generateToken(user);
        data.put("token", newToken.getValue());
        data.put("expiresAt", newToken.getExpiresAt());

        if (!user.getPassword().equals(request.getOldPassword())) {
            user.setFailedPasswordUpdates(user.getFailedPasswordUpdates() + 1);
            if (user.getFailedPasswordUpdates() >= 3) {
                user.setActive(false);
                userRepository.save(user);
                throw new ApiException("Account deactivated after 3 failed password update attempts", HttpStatus.FORBIDDEN);
            }
            userRepository.save(user);
            throw new ApiException("Old password is incorrect", HttpStatus.BAD_REQUEST);
        }

        String pw1 = request.getNewPassword1();
        String pw2 = request.getNewPassword2();
        if (!pw1.equals(pw2)) {
            throw new ApiException("The two new passwords must match", HttpStatus.BAD_REQUEST);
        }
        if (pw1.length() < 8 || !pw1.matches(".*[A-Z].*") || !pw1.matches(".*[a-z].*")
                || !pw1.matches(".*[0-9].*") || !pw1.matches(".*[^A-Za-z0-9].*")) {
            throw new ApiException("Password must be at least 8 characters and contain uppercase, lowercase, number, and special character.", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(pw1);
        user.setFailedPasswordUpdates(0);
        userRepository.save(user);

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
    public ApiResponse<Map<String, Object>> updateAccountStatus(UpdateAccountStatusRequest request) {
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.UNAUTHORIZED));
        tokenService.validateToken(request.getToken(), requester);

        if (requester.getPermanentRole() != PermanentRole.ADMIN) {
            throw new ApiException("Not authorized to update user accounts", HttpStatus.FORBIDDEN);
        }

        User targetUser = userRepository.findByUsername(request.getTargetUsername())
                .orElseThrow(() -> new ApiException("Target user not found", HttpStatus.NOT_FOUND));

        targetUser.setActive(request.getNewActive());
        userRepository.save(targetUser);

        if (!targetUser.isActive()) {
            tokenService.deactivateTokens(targetUser);
        }

        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "User account status updated successfully",
                new HashMap<>()
        );
    }

    // -------------------- DELETE USER --------------------
    /**
     * Deletes a user account. Admins can delete other users; Deletes all tokens
     * for the user.
     *
     * @param request delete user request
     * @return ApiResponse
     */
    public ApiResponse<Map<String, Object>> deleteUser(DeleteUserRequest request) {
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.UNAUTHORIZED));
        tokenService.validateToken(request.getToken(), requester);

        User targetUser;

        if (requester.getPermanentRole() != PermanentRole.ADMIN) {
            throw new ApiException("Not authorized to delete other users", HttpStatus.FORBIDDEN);
        }
        targetUser = userRepository.findByUsername(request.getTargetUsername())
                .orElseThrow(() -> new ApiException("Target user not found", HttpStatus.NOT_FOUND));

        tokenService.deleteTokens(targetUser);
        userRepository.delete(targetUser);

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
        User requester = userRepository.findByUsername(request.getRequesterUsername())
                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.UNAUTHORIZED));
        tokenService.validateToken(request.getToken(), requester);

        //invalidate current users token 
        tokenService.deactivateTokens(requester);
        return new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK.value(),
                "User logged out successfully",
                new HashMap<>()
        );
    }

    //-------------------- USER DEACTIVATION BAD TOKEN --------------------
    /**
     * Deactivates a user unless they are an ADMIN.
     *
     * @param user the user to deactivate
     */
    public void deactivateIfNotAdmin(User user) {
        if (user.getPermanentRole() != PermanentRole.ADMIN && user.isActive()) {
            user.setActive(false);
            userRepository.save(user);
        }
    }
}

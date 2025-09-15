package com.festivalmanager.controller;

import com.festivalmanager.dto.api.ApiResponse;
import com.festivalmanager.dto.user.DeleteUserRequest;
import com.festivalmanager.dto.user.RegisterRequest;
import com.festivalmanager.dto.user.LogoutRequest;
import com.festivalmanager.dto.user.LoginRequest;
import com.festivalmanager.dto.user.UpdatePasswordRequest;
import com.festivalmanager.dto.user.UpdateInfoRequest;
import com.festivalmanager.dto.user.UpdateAccountStatusRequest;
import com.festivalmanager.service.UserService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user-related operations, including registration,
 * authentication, information update, password update, account status
 * management, and user deletion.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Registers a new user in the system.
     *
     * @param request registration request containing username, full name, and
     * passwords
     * @return ApiResponse with operation status
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    /**
     * Authenticates a user and returns a token.
     *
     * @param request login request containing username and password
     * @return ApiResponse with token and expiration
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }

    /**
     * Updates user information such as username or full name.
     *
     * @param request update info request containing requester info, token, and
     * optional new data
     * @return ApiResponse with operation status and new token if applicable
     */
    @PostMapping("/updateuserinfo")
    public ApiResponse<Map<String, Object>> updateUserInfo(@RequestBody UpdateInfoRequest request) {
        return userService.updateUserInfo(request);
    }

    /**
     * Updates a user's password.
     *
     * @param request password update request containing old and new passwords
     * @return ApiResponse with operation status and new token
     */
    @PostMapping("/updateuserpassword")
    public ApiResponse<Map<String, Object>> updateUserPassword(@RequestBody UpdatePasswordRequest request) {
        return userService.updateUserPassword(request);
    }

    /**
     * Updates a user's account status (activation/deactivation).
     *
     * @param request account status update request containing target username
     * and new status
     * @return ApiResponse with operation status
     */
    @PostMapping("/updateaccountstatus")
    public ApiResponse<Map<String, Object>> updateAccountStatus(@RequestBody UpdateAccountStatusRequest request) {
        return userService.updateAccountStatus(request);
    }

    /**
     * Deletes a user account. Can be executed only by an
     * admin.
     *
     * @param request delete user request containing target username 
     * and requester info
     * @return ApiResponse with operation status
     */
    @PostMapping("/deleteuser")
    public ApiResponse<Map<String, Object>> deleteUser(@RequestBody DeleteUserRequest request) {
        return userService.deleteUser(request);
    }

    /**
     * Simple logout endpoint to logout a user (invalidates the current token
     * thus ending the session)
     *
     * @param request logout User request containing target username 
     * and requester info
     * @return ApiResponse with operation status
     */
    @PostMapping("/logout")
    public ApiResponse<Map<String, Object>> logoutUser(@RequestBody LogoutRequest request) {
        return userService.logOutUser(request);
    }

}

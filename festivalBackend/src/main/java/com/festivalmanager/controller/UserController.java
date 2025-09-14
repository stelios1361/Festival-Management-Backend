package com.festivalmanager.controller;

import com.festivalmanager.dto.ApiResponse;
import com.festivalmanager.dto.DeleteUserRequest;
import com.festivalmanager.dto.LoginRequest;
import com.festivalmanager.dto.RegisterRequest;
import com.festivalmanager.dto.UpdateAccountStatusRequest;
import com.festivalmanager.dto.UpdateInfoRequest;
import com.festivalmanager.dto.UpdatePasswordRequest;
import com.festivalmanager.service.UserService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing user-related operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Registers a new user.
     *
     * @param registerRequest
     * @return an {@link ApiResponse} containing the saved user or any error
     */
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody RegisterRequest registerRequest) {
        return userService.registerUser(registerRequest);
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }

    @PostMapping("/updateuserinfo")
    public ApiResponse<Map<String, Object>> updateUserInfo(@RequestBody UpdateInfoRequest updateinforequest) {
        return userService.updateUserInfo(updateinforequest);
    }

    @PostMapping("/updateuserpassword")
    public ApiResponse<Map<String, Object>> updateUserPassword(@RequestBody UpdatePasswordRequest updatepasswordrequest) {
        return userService.updateUserPassword(updatepasswordrequest);
    }

    @PostMapping("/updateaccountstatus")
    public ApiResponse<Map<String, Object>> updateAccountStatus(@RequestBody UpdateAccountStatusRequest updateaccountstatusrequest) {
        return userService.updateAccountStatus(updateaccountstatusrequest);
    }

    @PostMapping("/deleteuser")
    public ApiResponse<Map<String, Object>> deleteUser(@RequestBody DeleteUserRequest deleteuserRequest) {
        return userService.deleteUser(deleteuserRequest);
    }

}

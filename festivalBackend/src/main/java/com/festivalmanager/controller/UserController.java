package com.festivalmanager.controller;

import com.festivalmanager.dto.ApiResponse;
import com.festivalmanager.dto.LoginRequest;
import com.festivalmanager.model.User;
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
     * @param user the user object from request body
     * @return an {@link ApiResponse} containing the saved user or any error
     */
    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }

}

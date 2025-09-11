package com.festivalmanager.controller;

import com.festivalmanager.dto.ApiResponse;
import com.festivalmanager.model.User;
import com.festivalmanager.service.UserService;
import java.time.LocalDateTime;
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
        User savedUser = userService.registerUser(user);
        return new ApiResponse<>(
                LocalDateTime.now(),
                200,
                "User registered successfully",
                savedUser
        );
    }

}

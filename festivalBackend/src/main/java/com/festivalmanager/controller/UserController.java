package com.festivalmanager.controller;

import com.festivalmanager.dto.ApiResponse;
import com.festivalmanager.model.User;
import com.festivalmanager.service.UserService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


//controller and endpoints for users 


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    //if all of the requested fields and requirments are ment user is successfully registered 
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

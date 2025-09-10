package com.festivalmanager.service;

import com.festivalmanager.model.User;
import com.festivalmanager.model.PermanentRole;
import com.festivalmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        // validate username
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        // validate password 
        if (user.getPassword().length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long!");
        }

        // default role
        user.setPermanentRole(PermanentRole.USER);
        user.setActive(true);

        return userRepository.save(user);
    }
}

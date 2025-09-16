package com.festivalmanager.security;

import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.User;
import com.festivalmanager.repository.UserRepository;
import com.festivalmanager.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserSecurityService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public UserSecurityService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    /**
     * Validates requester: exists, active, and token is valid.
     * 
     * @param requesterUsername the username of the requester
     * @param token the token to validate
     * @return the validated User entity
     */
    public User validateRequester(String requesterUsername, String token) {
        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new ApiException("Requester not found", HttpStatus.UNAUTHORIZED));

        if (!requester.isActive()) {
            throw new ApiException("Account is deactivated. Please contact admin.", HttpStatus.FORBIDDEN);
        }

        tokenService.validateToken(token, requester);

        return requester;
    }
}

package com.festivalmanager.security;

import com.festivalmanager.exception.ApiException;
import com.festivalmanager.model.User;
import com.festivalmanager.repository.UserRepository;
import com.festivalmanager.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Service class for handling user security-related operations, such as
 * validating user access and verifying authentication tokens.
 * 
 * <p>This service ensures that a requester exists, is active, and has a valid
 * authentication token before allowing access to secure operations.</p>
 */
@Service
public class UserSecurityService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    /**
     * Constructs a new {@link UserSecurityService} with required dependencies.
     *
     * @param userRepository the repository to access user data
     * @param tokenService   the service to validate authentication tokens
     */
    public UserSecurityService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    /**
     * Validates a requester by checking that the user exists, is active, and
     * that the provided token is valid.
     *
     * @param requesterUsername the username of the requester
     * @param token             the authentication token to validate
     * @return the validated {@link User} entity
     * @throws ApiException if the user does not exist, is deactivated, or
     *                      the token is invalid
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

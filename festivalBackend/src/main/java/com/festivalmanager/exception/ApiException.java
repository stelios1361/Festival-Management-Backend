package com.festivalmanager.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception for API errors.
 * <p>
 * Wraps an error message with an HTTP status code.
 */
public class ApiException extends RuntimeException {

    /**
     * HTTP status code associated with this exception.
     */
    private final HttpStatus status;

    /**
     * Constructs a new ApiException with the specified message and status.
     *
     * @param message the error message
     * @param status  the HTTP status code
     */
    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Gets the HTTP status code.
     *
     * @return the HTTP status
     */
    public HttpStatus getStatus() {
        return status;
    }
}

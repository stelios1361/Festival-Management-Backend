package com.festivalmanager.exception;

import org.springframework.http.HttpStatus;

// Generic exception for all API/domain errors
public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

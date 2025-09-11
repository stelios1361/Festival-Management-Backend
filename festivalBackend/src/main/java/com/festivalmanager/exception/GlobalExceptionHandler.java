package com.festivalmanager.exception;

import com.festivalmanager.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Global exception handler to catch and format all exceptions consistently.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ApiException and builds a structured API response.
     *
     * @param ex the ApiException thrown
     * @return a ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        ApiResponse<Void> response = new ApiResponse<>(
                LocalDateTime.now(),
                ex.getStatus().value(),
                ex.getMessage(),
                null
        );
        return new ResponseEntity<>(response, ex.getStatus());
    }

    /**
     * Handles unexpected RuntimeExceptions.
     *
     * @param ex the RuntimeException thrown
     * @return a ResponseEntity containing the ApiResponse
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<Void> response = new ApiResponse<>(
                LocalDateTime.now(),
                500,
                ex.getMessage(),
                null
        );
        return ResponseEntity.internalServerError().body(response);
    }
}

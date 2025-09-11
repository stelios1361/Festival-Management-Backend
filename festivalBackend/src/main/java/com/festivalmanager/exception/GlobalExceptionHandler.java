package com.festivalmanager.exception;

import com.festivalmanager.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;

// main global exception handler to have all of the backends exceptions generated
// and controlled from a single place 


@ControllerAdvice
public class GlobalExceptionHandler {

    // Helper method to build structured API responses
    private <T> ResponseEntity<ApiResponse<T>> buildResponse(org.springframework.http.HttpStatus status, String message) {
        ApiResponse<T> response = new ApiResponse<>(
                LocalDateTime.now(),
                status.value(),
                message,
                null
        );
        return new ResponseEntity<>(response, status);
    }

    // Handle all ApiException instances
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        return buildResponse(ex.getStatus(), ex.getMessage());
    }

    // Fallback for unexpected RuntimeExceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        return buildResponse(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}

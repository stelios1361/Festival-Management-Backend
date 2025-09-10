package com.festivalmanager.exception;

import com.festivalmanager.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;

// main global exception handler to have all of the backends exceptions generated
// and controlled from a single place 
@ControllerAdvice
public class GlobalExceptionHandler {

    // generic response entity to build up the response to send from the backend 
    private <T> ResponseEntity<ApiResponse<T>> buildResponse(HttpStatus status, String message) {
        ApiResponse<T> response = new ApiResponse<>(
                LocalDateTime.now(),
                status.value(),
                message,
                null
        );
        return new ResponseEntity<>(response, status);
    }

    // handle all the custom exceptions 
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserExists(UserAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidUsername(InvalidUsernameException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPassword(InvalidPasswordException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // fallback for unexpected RuntimeExceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(RuntimeException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    
    
    //private classes to implement the custom exceptions 
    
    private class UserAlreadyExistsException extends RuntimeException {

        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    private class InvalidUsernameException extends RuntimeException {

        public InvalidUsernameException(String message) {
            super(message);
        }
    }

    private class InvalidPasswordException extends RuntimeException {

        public InvalidPasswordException(String message) {
            super(message);
        }
    }

}

package com.festivalmanager.dto.api;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper for all backend responses.
 *
 * @param <T> the type of data contained in the response
 */
public class ApiResponse<T> {

    /**
     * Timestamp when the response was created.
     */
    private LocalDateTime timestamp;

    /**
     * HTTP status code of the response.
     */
    private int status;

    /**
     * Human-readable message describing the response.
     */
    private String message;

    /**
     * Actual data returned by the API.
     */
    private T data;

    /**
     * Constructs a new ApiResponse.
     *
     * @param timestamp the timestamp of the response
     * @param status    the HTTP status code
     * @param message   the descriptive message
     * @param data      the response data
     */
    public ApiResponse(LocalDateTime timestamp, int status, String message, T data) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters and setters

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

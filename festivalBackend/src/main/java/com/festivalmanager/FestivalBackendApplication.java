package com.festivalmanager;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Festival Manager Backend application.
 */
@SpringBootApplication
public class FestivalBackendApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(FestivalBackendApplication.class, args);
    }
}

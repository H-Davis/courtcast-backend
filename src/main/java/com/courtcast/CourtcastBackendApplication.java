package com.courtcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * File created by Hananiah Davis on Apr 29, 2025
 */
@SpringBootApplication
@EnableCaching
public class CourtcastBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourtcastBackendApplication.class, args);
    }
}

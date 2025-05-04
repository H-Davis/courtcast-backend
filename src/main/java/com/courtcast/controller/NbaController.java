package com.courtcast.controller;

import com.courtcast.service.NbaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * File created by Hananiah Davis on May 03, 2025
 */
@RestController
@RequestMapping("/api/nba")
public class NbaController {
    private final NbaService nbaService;

    public NbaController(NbaService nbaService) {
        this.nbaService = nbaService;
    }

    @GetMapping("/gamesByDate")
    public ResponseEntity<?> getGamesByDate(@RequestParam String date) {
        System.out.println("Received date: " + date);
        return ResponseEntity.ok(nbaService.getGamesByDate(date));
    }
}

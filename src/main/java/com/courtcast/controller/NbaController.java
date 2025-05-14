package com.courtcast.controller;

import com.courtcast.service.NbaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * File created by Hananiah Davis on May 03, 2025
 */
@RestController
@RequestMapping("/api/nba")
@CrossOrigin(origins = "http://localhost:3000")
public class NbaController {
    private final NbaService nbaService;

    public NbaController(NbaService nbaService) {
        this.nbaService = nbaService;
    }

    @GetMapping("/games")
    public ResponseEntity<?> getGamesByDate(@RequestParam String date) {
        return ResponseEntity.ok(nbaService.getGamesByDate(date));
    }

    @GetMapping("/players/statistics")
    public ResponseEntity<Object> getPlayerStats(@RequestParam("game") String gameId) {
        Object stats = nbaService.getGameStats(gameId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/players")
    public ResponseEntity<Object> getPlayersByName(@RequestParam String search) {
        return ResponseEntity.ok(nbaService.getPlayersByName(search));
    }

}

package com.courtcast.controller;

import com.courtcast.service.NbaService;
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

    @GetMapping("/games/date")
    public ResponseEntity<?> getGamesByDate(@RequestParam String date) {
        return ResponseEntity.ok(nbaService.getGamesByDate(date));
    }

    @GetMapping("/games/team")
    public ResponseEntity<?> getGamesByTeam(@RequestParam String season, @RequestParam String team) {
        return ResponseEntity.ok(nbaService.getGamesByTeam(season, team));
    }

    @GetMapping("/players/statistics")
    public ResponseEntity<Object> getPlayerStats(@RequestParam("game") String gameId) {
        Object stats = nbaService.getGameStats(gameId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/players/search")
    public ResponseEntity<Object> getPlayersByName(@RequestParam String search) {
        return ResponseEntity.ok(nbaService.getPlayersByName(search));
    }

    @GetMapping("/players/team")
    public ResponseEntity<Object> getPlayersByTeamId(
            @RequestParam String team,
            @RequestParam String season) {
        return ResponseEntity.ok(nbaService.getPlayersByTeamId(team, season));
    }

    @GetMapping("/teams")
    public ResponseEntity<Object> getTeamsById(@RequestParam String id) {
        return ResponseEntity.ok(nbaService.getTeamById(id));
    }

    @GetMapping("/standings/conference")
    public ResponseEntity<Object> getStandingsByConference(@RequestParam String league, @RequestParam String season, @RequestParam String conference) {
        return ResponseEntity.ok(nbaService.getStandingsByConference(league, season, conference));
    }

    @GetMapping("/standings/division")
    public ResponseEntity<Object> getStandingsByDivision(@RequestParam String league, @RequestParam String season, @RequestParam String division) {
        return ResponseEntity.ok(nbaService.getStandingsByDivision(league, season, division));
    }

}

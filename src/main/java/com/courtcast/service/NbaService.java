package com.courtcast.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cache.annotation.Cacheable;


/**
 * File created by Hananiah Davis on May 03, 2025
 */
@Service
public class NbaService {

    @Value("${nbaapi.key}")
    private String apiKey;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api-nba-v1.p.rapidapi.com")
                .defaultHeader("X-RapidAPI-Key", apiKey)
                .defaultHeader("X-RapidAPI-Host", "api-nba-v1.p.rapidapi.com")
                .build();
    }

    @Cacheable(value = "gameByDateCache", key = "#date")
    public Object getGamesByDate(String date) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games")
                        .queryParam("date", date)
                        .build()
                )
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    @Cacheable(value = "gameStatsCache", key = "#gameId")
    public Object getGameStats(String gameId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/players/statistics")
                        .queryParam("game", gameId)
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public Object getPlayersByName(String search) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/players")
                        .queryParam("search", search)
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }
}

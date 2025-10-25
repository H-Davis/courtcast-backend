package com.courtcast.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * File created by Hananiah Davis on May 03, 2025
 */
@Service
public class NbaService {

    @Value("${nbaapi.key}")
    private String apiKey;

    private WebClient webClient;

    private static final ZoneId NBA_ZONE = ZoneId.of("America/New_York");

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
        try {
            LocalDate targetEasternDate = LocalDate.parse(date);
            LocalDate nextDate = targetEasternDate.plusDays(1);

            Map<String, Object> day1 = fetchGamesRawWithRetry(date);
            Map<String, Object> day2 = fetchGamesRawWithRetry(nextDate.toString());

            List<Map<String, Object>> allGames = new ArrayList<>();
            if (day1 != null) {
                Object resp = day1.get("response");
                if (resp instanceof List<?> list) {
                    allGames.addAll((List<Map<String, Object>>) list);
                }
            }
            if (day2 != null) {
                Object resp = day2.get("response");
                if (resp instanceof List<?> list) {
                    allGames.addAll((List<Map<String, Object>>) list);
                }
            }

            // 🔍 Debug: log how many total before filtering
            System.out.println("Fetched total games before filter: " + allGames.size());

            List<Map<String, Object>> filtered = filterByEasternDate(allGames, targetEasternDate);

            System.out.println("After Eastern filter: " + filtered.size());

            Map<String, Object> result = new HashMap<>();
            result.put("get", "games/");
            result.put("parameters", Map.of("date", date));
            result.put("results", filtered.size());
            result.put("response", filtered);

            return result;

        } catch (WebClientResponseException.TooManyRequests e) {
            System.err.println("Hit RapidAPI rate limit — backing off temporarily");
            return Map.of("error", "Rate limited by NBA API");
        }
    }

    private Map<String, Object> fetchGamesRawWithRetry(String date) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/games")
                        .queryParam("date", date)
                        .build())
                .retrieve()
                .onStatus(HttpStatus.TOO_MANY_REQUESTS::equals, resp -> {
                    System.out.println("Received 429 Too Many Requests from NBA API");
                    return resp.createException();
                })
                .bodyToMono(Map.class)
                // retry with exponential backoff if API throttles
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(ex -> ex instanceof WebClientResponseException.TooManyRequests))
                .block();
    }

    private List<Map<String, Object>> filterByEasternDate(List<Map<String, Object>> games, LocalDate targetEasternDate) {
        return games.stream()
                .filter(game -> {
                    try {
                        Map<String, Object> dateMap = (Map<String, Object>) game.get("date");
                        String startIso = (String) dateMap.get("start");
                        if (startIso == null) return false;

                        Instant startInstant = Instant.parse(startIso);
                        ZonedDateTime easternTime = startInstant.atZone(ZoneId.of("America/New_York"));

                        boolean matches = easternTime.toLocalDate().isEqual(targetEasternDate);
                        if (matches) {
                            System.out.println("Keeping game starting " + easternTime + " ET for " + targetEasternDate);
                        }
                        return matches;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
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

    @Cacheable(value = "playersByNameCache", key = "#search")
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

    @Cacheable(value = "teamByIdCache", key = "#id")
    public Object getTeamById(String id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/teams")
                        .queryParam("id", id)
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    @Cacheable(value = "playersByTeamIdCache", key = "#team + '-' + #year")
    public Object getPlayersByTeamId(String team, String year) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/players")
                        .queryParam("team", team)
                        .queryParam("season", year)
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    @Cacheable(value = "standingsByConferenceCache", key = "#league + '-' + #season + '-' + #conference")
    public Object getStandingsByConference(String league, String season, String conference) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/standings")
                        .queryParam("league", league)
                        .queryParam("season", season)
                        .queryParam("conference", conference)
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    @Cacheable(value = "standingsByDivisionCache", key = "#league + '-' + #season + '-' + #division")
    public Object getStandingsByDivision(String league, String season, String division) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/standings")
                        .queryParam("league", league)
                        .queryParam("season", season)
                        .queryParam("division", division)
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    @Cacheable(value = "gamesByTeamCache", key = "#season + '-' + #team")
    public Object getGamesByTeam(String season, String team) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games")
                        .queryParam("season", season)
                        .queryParam("team", team)
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }
}

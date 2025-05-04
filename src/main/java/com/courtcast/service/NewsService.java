package com.courtcast.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class NewsService {

    @Value("${newsapi.key}")
    private String apiKey;

    public List<Map<String, Object>> fetchNbaNews() {
        String url = "https://newsapi.org/v2/everything?q=+nba AND (playoff OR playoffs)&language=en&sortBy=publishedAt&apiKey=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        return (List<Map<String, Object>>) response.getBody().get("articles");
    }
}

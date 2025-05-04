package com.courtcast.controller;

import com.courtcast.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/nba")
    public ResponseEntity<?> getNbaNews() {
        return ResponseEntity.ok(newsService.fetchNbaNews());
    }
}
